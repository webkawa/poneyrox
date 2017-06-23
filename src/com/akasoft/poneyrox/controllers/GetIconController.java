package com.akasoft.poneyrox.controllers;

import com.akasoft.poneyrox.exceptions.RequestException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

/**
 *  Controleur permettant la récupération des icones.
 */
@Controller
public class GetIconController {
    /**
     *  Contexte du controlleur.
     */
    @Autowired
    private ServletContext servletContext;

    /**
     *  Retourne une icone.
     *  @param response Réponse HTTP.
     *  @param key Clef d'accès.
     *  @param size Taille en pixels.
     *  @param reverse Inversion des couleurs.
     *  @throws RequestException En cas d'erreur du controleur.
     */
    @RequestMapping("media/{key}/{size}/{reverse}")
    public void execute(
            @Autowired HttpServletResponse response,
            @PathVariable("key") String key,
            @PathVariable("size") int size,
            @PathVariable("reverse") boolean reverse) throws RequestException {
        /* Vérification de la taille */
        if (size != 96 && size != 64 && size != 48 && size != 32 && size != 24 && size != 16 && size != 12 && size != 8) {
            throw new RequestException("Invalid size %dpx", size);
        }

        try {
            /* Récupération du fichier */
            String target = String.format("%s/%s",
                    this.servletContext.getRealPath("/WEB-INF/icons"),
                    String.format("%s-%d-%s.png", key, size, reverse ? "white" : "black"));
            File pre = new File(target);

            /* Vérification d'existence */
            if (pre.exists()) {
                IOUtils.copy(new FileInputStream(pre), response.getOutputStream());
            } else {
                /* Récupération des informations utiles */
                String source = String.format("%s/%s",
                        this.servletContext.getRealPath("/WEB-INF/icons"),
                        String.format("%s.png", key));
                File post = new File(source);
                BufferedImage image = ImageIO.read(post);

                /* Inversion */
                /* Voir : https://www.dyclassroom.com/image-processing-project/how-to-convert-a-color-image-into-negative */
                if (reverse) {
                    int width = image.getWidth();
                    int height = image.getHeight();
                    for(int y = 0; y < height; y++){
                        for(int x = 0; x < width; x++){
                            int p = image.getRGB(x,y);
                            int a = (p>>24)&0xff;
                            int r = (p>>16)&0xff;
                            int g = (p>>8)&0xff;
                            int b = p&0xff;
                            //subtract RGB from 255
                            r = 255 - r;
                            g = 255 - g;
                            b = 255 - b;
                            //set new RGB value
                            p = (a<<24) | (r<<16) | (g<<8) | b;
                            image.setRGB(x, y, p);
                        }
                    }
                }

                /* Redimensionnement */
                if (size != 96) {
                    image = this.getScaledInstance(image, size, size, true);
                }

                /* Ecriture */
                ImageIO.write(image, "png", pre);

                /* Renvoi */
                IOUtils.copy(new FileInputStream(pre), response.getOutputStream());
            }
        } catch (Exception ex) {
            throw new RequestException(ex, "Failed to retrieve icon");
        }
    }

    /**
     *  Retourne une icone avec les couleurs par défaut.
     *  @param response Réponse HTTP.
     *  @param key Clef d'accès.
     *  @param size Taille en pixels.
     *  @throws RequestException En cas d'erreur du controleur.
     */
    @RequestMapping("media/{key}/{size}")
    public void execute(
            @Autowired HttpServletResponse response,
            @PathVariable("key") String key,
            @PathVariable("size") int size) throws RequestException {
        this.execute(response, key, size, true);
    }

    /**
     *  Retourne une icone avec les couleurs par défaut et la taille maximale.
     *  @param response Réponse HTTP.
     *  @param key Clef d'accès.
     *  @throws RequestException En cas d'erreur du controleur.
     */
    @RequestMapping("media/{key}")
    public void execute(
            @Autowired HttpServletResponse response,
            @PathVariable("key") String key) throws RequestException {
        this.execute(response, key, 96, true);
    }

    /**
     *  Voir : https://stackoverflow.com/questions/7951290/re-sizing-an-image-without-losing-quality
     *  @param img Image traitée.
     *  @param targetWidth Largeur souhaitée.
     *  @param targetHeight Hauteur souhaitée.
     *  @param higherQuality Qualité maximale.
     *  @return Image redimensionnée.
     */
    private BufferedImage getScaledInstance(
            BufferedImage img,
            int targetWidth,
            int targetHeight,
            boolean higherQuality) {
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
                BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }
}
