package com.example.demo.beens.impls;

import com.example.demo.beens.interfaces.SaveToDock;
import com.example.demo.classes.Album;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;


@Component("albumToDock")
public class SaveToDockImpl implements SaveToDock {
    private static final Logger log = Logger.getLogger(SaveToDockImpl.class);
    @Value("${doc.template}")
    String templateFileName;
    @Value("${doc.save.dir}")
    String savePath;

    @Override
    public void save(Album album) {
        try(InputStream fis = new FileInputStream(templateFileName)) {
            XWPFDocument doc = new XWPFDocument(fis);
            replaceText(doc,"$albumName", album.getName());
            replaceText(doc,"$artistName", album.getAuthor());
            replaceText(doc,"$genre", album.getGenre());
            replaceImg(doc, "$img", album.getPictureURL());
            formTrackTable(doc, album.getSongs());
            saveFile(album.getName() + "_" + album.getAuthor(),doc);
        } catch (FileNotFoundException e) {
            log.error("Failed to find template", e);
        } catch (IOException e) {
            log.error("Failed to open stream", e);
        }
    }

    private void replaceText(XWPFDocument doc, String findText, String replaceText) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains(findText)) {
                        text = text.replace(findText, replaceText);
                        r.setText(text, 0);
                    }
                }
            }
        }
    }

    private void replaceImg(XWPFDocument doc, String findText, String imageURL){
        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains(findText)) {
                        r.setText("",0);
                        r.addBreak();
                        try {
                            URL url = new URL(imageURL);
                            InputStream in = new BufferedInputStream(url.openStream());
                            r.addPicture(in, XWPFDocument.PICTURE_TYPE_PNG, imageURL, Units.toEMU(174), Units.toEMU(174));
                            in.close();
                        } catch (MalformedURLException e) {
                            log.error("Failed to form Url", e);
                        } catch (IOException e) {
                            log.error("Failed to open image", e);
                        } catch (InvalidFormatException e) {
                            log.error("Invalid image format", e);
                        }
                    }
                }
            }
        }
    }

    private void formTrackTable(XWPFDocument doc, Map<String,Integer> tracks){
        XWPFTable table = doc.getTableArray(0);
        for (Map.Entry<String, Integer> entry : tracks.entrySet()) {
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(entry.getKey());
            row.getCell(1).setText(entry.getValue()+"");
        }
    }

    private void saveFile(String fileName, XWPFDocument doc){
        try(OutputStream out = new FileOutputStream(fileName+".docx")) {
            doc.write(out);
        } catch (FileNotFoundException e) {
            log.error("Failed to find file", e);
        } catch (IOException e) {
            log.error("Failed to open stream", e);
        }
    }
}
