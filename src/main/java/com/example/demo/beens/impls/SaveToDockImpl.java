package com.example.demo.beens.impls;

import com.example.demo.beens.interfaces.SaveToDock;
import com.example.demo.classes.Album;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
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
    private String templateFileName;
    @Value("${doc.save.dir}")
    private String savePath;
    @Value("${doc.extension}")
    private String fileExtension;

    @Override
    public String save(List<Album> albums, String name) {
        XWPFDocument document = saveOne(albums.get(0));
        for (int i=1; i < albums.size(); i++) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setPageBreak(true);
            XWPFDocument tempDoc = saveOne(albums.get(i));
            mergeWord(document,tempDoc);
        }

        String path = saveFile(name,document);
        XWPFDocument doc = null;
        try(InputStream fis = new FileInputStream(path)) {
            doc = new XWPFDocument(fis);
            for (Album album:
                    albums) {
                replaceImg(doc,"$img"+album.getPictureURL(),album.getPictureURL());
            }
        } catch (FileNotFoundException e) {
            log.error("Failed to find file with imageVars", e);
        } catch (IOException e) {
            log.error("Failed to open stream", e);
        }
        saveFile(name,doc);
        return path;
    }

    private XWPFDocument saveOne(Album album){
        XWPFDocument doc = null;
        try(InputStream fis = new FileInputStream(templateFileName)) {
            doc = new XWPFDocument(fis);
            replaceText(doc,"$albumName", album.getName());
            replaceText(doc,"$artistName", album.getAuthor());
            replaceText(doc,"$genre", album.getGenre());
            replaceText(doc,"$img","$img"+album.getPictureURL());
            formTrackTable(doc, album.getSongs());
        } catch (FileNotFoundException e) {
            log.error("Failed to find template", e);
        } catch (IOException e) {
            log.error("Failed to open stream", e);
        }
        return doc;
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

    private String saveFile(String fileName, XWPFDocument doc){
        String fileStr= savePath+fileName+fileExtension;
        File file = new File(fileStr);
        file.getParentFile().mkdirs();
        try(OutputStream out = new FileOutputStream(file)) {
            doc.write(out);
        } catch (FileNotFoundException e) {
            log.error("Failed to find file", e);
        } catch (IOException e) {
            log.error("Failed to open stream", e);
        }
        return fileStr;
    }

    private void mergeWord(XWPFDocument document1, XWPFDocument document2){
        CTBody src1Body = document1.getDocument().getBody();
        CTBody src2Body = document2.getDocument().getBody();
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String appendString = src2Body.xmlText(optionsOuter);
        String srcString = src1Body.xmlText();
        String prefix = srcString.substring(0,srcString.indexOf(">")+1);
        String mainPart = srcString.substring(srcString.indexOf(">")+1,srcString.lastIndexOf("<"));
        String suffix = srcString.substring( srcString.lastIndexOf("<") );
        String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
        CTBody makeBody = null;
        try {
            makeBody = CTBody.Factory.parse(prefix+mainPart+addPart+suffix);
        } catch (XmlException e) {
            log.error("Failed to merge XWPFDocuments",e);
        }
        src1Body.set(makeBody);
    }
}
