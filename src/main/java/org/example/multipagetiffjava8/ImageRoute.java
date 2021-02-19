package org.example.multipagetiffjava8;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ImageRoute extends RouteBuilder implements Processor {

    Logger LOG = Logger.getLogger(ImageRoute.class.getName());

    @Value("${image.folder.tiff}")
    private String folderTiff;

    @Value("${image.folder.png}")
    private String folderPng;

    @Override
    public void configure() throws Exception {

        LOG.log(Level.INFO, "Reading tiff from {0}", folderTiff);
        LOG.log(Level.INFO, "Writing png to {0}", folderPng);

        fromF("file:%s?noop=true&idempotent=true", folderTiff)
                .process(this::process)
                .split(body())
                .toF("file:%s?fileName=${header.CamelFileNameOnly}_${header.CamelSplitIndex}.png", folderPng)
                .log("Page ${header.CamelSplitIndex} of file ${header.CamelFileNameOnly} processed");
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        File file = exchange.getIn().getBody(File.class);

        ImageInputStream is = ImageIO.createImageInputStream(file);
        Iterator<ImageReader> iterator = ImageIO.getImageReaders(is);
        ImageReader reader = iterator.next();
        reader.setInput(is);
        int nbPages = reader.getNumImages(true);

        List<OutputStream> pages = IntStream.range(0, nbPages)
                .mapToObj(i -> getPageOutputStream(reader, i)).collect(Collectors.toList());
        exchange.getIn().setBody(pages);
    }

    private OutputStream getPageOutputStream(ImageReader reader, int page){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            BufferedImage img = reader.read(page);
            ImageIO.write(img, "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream;
    }
}
