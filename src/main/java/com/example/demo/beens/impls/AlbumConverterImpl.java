package com.example.demo.beens.impls;

import com.example.demo.beens.interfaces.AlbumConverter;
import com.example.demo.classes.Album;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component("albumConverter")
public class AlbumConverterImpl implements AlbumConverter {
    @Override
    public String toXML(List<Album> albums){
        String res = null;
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element baseRoot = document.createElement("albums");
            document.appendChild(baseRoot);

            for (Album album:albums) {
                appendAlbumXml(baseRoot,document,album);
            }

            Element similar = document.createElement("totalAlbums");
            similar.appendChild(document.createTextNode(albums.get(0).getSimilarResults()+""));
            baseRoot.appendChild(similar);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult streamResult = new StreamResult(writer);
            transformer.transform(domSource, streamResult);
            res = writer.getBuffer().toString();
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return res;

    }

    private void appendAlbumXml(Element baseRoot, Document document, Album album){
        Element root = document.createElement("album");
        baseRoot.appendChild(root);

        Element name = document.createElement("name");
        name.appendChild(document.createTextNode(album.getName()));
        root.appendChild(name);

        Element artist = document.createElement("artist");
        artist.appendChild(document.createTextNode(album.getAuthor()));
        root.appendChild(artist);

        Element genre = document.createElement("genre");
        genre.appendChild(document.createTextNode(album.getGenre()));
        root.appendChild(genre);

        Element pictureURL = document.createElement("pictureURL");
        pictureURL.appendChild(document.createTextNode(album.getPictureURL()));
        root.appendChild(pictureURL);

        Element tracks = document.createElement("tracks");
        root.appendChild(tracks);

        Map<String, Integer> tracksMap = album.getSongs();
        for (Map.Entry<String, Integer> entry : tracksMap.entrySet()) {
            Element track = document.createElement("track");
            track.appendChild(document.createTextNode(entry.getKey()));
            tracks.appendChild(track);
            Attr attr = document.createAttribute("duration");
            attr.setValue(entry.getValue().toString());
            track.setAttributeNode(attr);
        }
    }

    @Override
    public String toJSON(List<Album> albums) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray albumsJson = new JSONArray();
            for (Album album: albums) {
                albumsJson.put(albumToJson(album));
            }
            jsonObject.put("albums", albumsJson);
            jsonObject.put("total albums",albums.get(0).getSimilarResults());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public JSONObject albumToJson(Album album){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("album",album.getName());
            jsonObject.put("artist",album.getAuthor());
            jsonObject.put("picture", album.getPictureURL());
            jsonObject.put("genre",album.getGenre());
            JSONArray tracks = new JSONArray();
            Map<String, Integer> tracksMap = album.getSongs();
            for (Map.Entry<String, Integer> entry : tracksMap.entrySet()) {
                JSONObject track = new JSONObject();
                track.put("name",entry.getKey());
                track.put("duration",entry.getValue());
                tracks.put(track);
            }
            jsonObject.put("tracks",tracks);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}


