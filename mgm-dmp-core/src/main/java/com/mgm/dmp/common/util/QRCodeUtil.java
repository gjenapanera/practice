package com.mgm.dmp.common.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.xerces.impl.dv.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public final class QRCodeUtil {

    protected static final Logger LOG = LoggerFactory.getLogger(QRCodeUtil.class);
    
    private QRCodeUtil(){
    	
    }
    
	public static String generateQRCode(String barCode){
        StringBuffer qrCode = new StringBuffer();
        int width = 150;
        int height = 125;
        String imageType = "png";
        try {
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(barCode, BarcodeFormat.QR_CODE, width, height, hintMap);
            int qrCodeWidth = byteMatrix.getWidth();
            int qrCodeHeight = byteMatrix.getHeight();
            
            BufferedImage image = new BufferedImage(qrCodeWidth, qrCodeHeight, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();
 
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, qrCodeWidth, qrCodeHeight);
            graphics.setColor(Color.BLACK);
 
            for (int i = 0; i < qrCodeWidth; i++) {
                for (int j = 0; j < qrCodeHeight; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, imageType, baos);
            baos.flush();
            qrCode.append("data:image/png;base64,").append(Base64.encode(baos.toByteArray()));
            baos.close();
        } catch (WriterException e) {
            LOG.error("Exception while executing", e);
        } catch (IOException e) {
            LOG.error("Exception while executing", e);
		}
        return qrCode.toString();
	}

}
