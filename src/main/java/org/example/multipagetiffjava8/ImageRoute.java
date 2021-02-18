package org.example.multipagetiffjava8;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ImageRoute extends RouteBuilder implements Processor {

    @Override
    public void configure() throws Exception {
        from("file:images_tiff?noop=true&idempotent=true")
                .process(this::process)
                .split(body())
                .to("file:images_png?fileName=${header.CamelFileNameOnly}_${header.CamelSplitIndex}.png")
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
