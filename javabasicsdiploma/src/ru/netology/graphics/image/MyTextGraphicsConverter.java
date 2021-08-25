package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class MyTextGraphicsConverter implements TextGraphicsConverter {
    private double maxRatio;
    private int maxWidth = 0;
    private int maxHeight = 0;
    TextColorSchema schema;

    @Override
    public String convert(java.lang.String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));
        checkRatio(img);

        int multiplier = getImgMultiplier(img);
        int newWidth = img.getWidth() / multiplier; // новая ширина с учетом коэф.
        int newHeight = img.getHeight() / multiplier; // новая высота с учетом коэф.

        BufferedImage bwImg = getEditedImg(img, newWidth, newHeight);
        char[][] pictureOfChars = convertImgByChars(bwImg, schema);
        return compileImgWithChars(pictureOfChars);
    }

    private void checkRatio(BufferedImage img) throws BadImageSizeException {
        //проверяем отношение ширины к высоте в double и сравниваем с maxratio
        double ourRatio = (double) img.getWidth() / img.getHeight();
        ourRatio = Math.round(ourRatio * 10.0) / 10.0;
        if (ourRatio > maxRatio) {
            throw new BadImageSizeException(ourRatio, maxRatio);
        }
    }

    private int getImgMultiplier(BufferedImage img) {
        //метод вычисляет коэффициент - во сколько раз нужно уменьшить картинку? если не нужно, то return 1
        int multiplier = 1;
        int widthDifference = img.getWidth() > maxWidth ? img.getWidth() - maxWidth : 0;
        int heightDifference = img.getHeight() > maxHeight ? img.getHeight() - maxHeight : 0;
        if (widthDifference != 0 || heightDifference != 0) {
            multiplier = widthDifference > heightDifference ?
                    (int) Math.ceil((double) img.getWidth() / maxWidth) :
                    (int) Math.ceil((double) img.getHeight() / maxHeight);
        }
        return multiplier;
    }

    private BufferedImage getEditedImg(BufferedImage img, int width, int height) {
        //метод возвращает отредактированное черно-белое изображение, учитывая все параметры
        Image scaledImage = img.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
        BufferedImage bwImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        return bwImg;
    }

    private char[][] convertImgByChars(BufferedImage bwImg, TextColorSchema schema) {
        //метод возвращает массив символов, учитывая выбранную colorSchema и оттенок черно-белого пикселя
        //по умолчанию используется MyTextColorSchema
        char[][] pictureOfChars = new char[bwImg.getHeight()][bwImg.getWidth()];
        //есть ли переданная textColorScheme? если нет то MyTextColorSchema
        if (schema == null) {
            schema = new MyTextColorSchema();
        }

        for (int x = 0; x < bwImg.getWidth(); x++) {
            for (int y = 0; y < bwImg.getHeight(); y++) {
                Color color = new Color(bwImg.getRGB(x, y));
                char c = schema.convert(color.getRed());
                pictureOfChars[y][x] = c;
            }
        }
        return pictureOfChars;
    }

    private String compileImgWithChars (char[][] pictureOfChars) {
        //метод возвращает строку из символов, взятых из convertImgByChars
        StringBuilder stringBuilder = new StringBuilder();
        for (char[] aChar : pictureOfChars) {
            for (char c : aChar) {
                stringBuilder.append(c);
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @java.lang.Override
    public void setMaxWidth(int width) {
        this.maxWidth = width;
    }

    @java.lang.Override
    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }

    @java.lang.Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @java.lang.Override
    public void setTextColorSchema(ru.netology.graphics.image.TextColorSchema schema) {
        this.schema = schema;
    }
}
