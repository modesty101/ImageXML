package com.niko.crypt.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 이미지 파일을 Base64로 인코딩하여 XML 파일에 저장합니다. 저장된 XML 파일 안에 있는 인코딩값을 추출하여, 원본의 복사본을
 * 생성합니다.
 * 
 * @author <a href="mailto:modesty101@daum.net">김동규</a>
 * @since 2017
 */
public class ImageXML {

	/**
	 * Read XML Document
	 * 
	 * @return doc
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public Document readXML() throws ParserConfigurationException, SAXException, IOException {
		// image.xml 파일은 미리 준비합니다
		File file = new File("image.xml");

		DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
		Document doc = docBuild.parse(file);
		doc.getDocumentElement().normalize();

		System.out.println("Root element : " + doc.getDocumentElement().getNodeName());

		return doc;
	}

	/**
	 * Append encoded value to image.xml
	 * 
	 * @return image
	 * @throws Exception
	 */
	public Node appendXML() throws Exception {
		Document doc = readXML();
		String encodedImage = encodeImage();
		NodeList imageXmlList = doc.getElementsByTagName("imageXML");
		Node image = null;
		for (int i = 0; i < imageXmlList.getLength(); i++) {

			System.out.println("---------- IMAGEXML " + i + "번째 ------------------");

			Node imageNode = imageXmlList.item(i);

			if (imageNode.getNodeType() == Node.ELEMENT_NODE) {
				// person엘리먼트
				Element imageXmlElmnt = (Element) imageNode;
				// image 태그
				NodeList imageList = imageXmlElmnt.getElementsByTagName("image");
				Element imageElmnt = (Element) imageList.item(0);
				image = imageElmnt.getFirstChild();
				image.setTextContent(encodedImage); // store it inside node

				System.out.println("image : " + image.getNodeValue());

				// image.setTextContent(encodedImage); // store it inside node
				TransformerFactory transFactory = TransformerFactory.newInstance();
				Transformer transformer = transFactory.newTransformer();
				DOMSource source = new DOMSource(imageXmlElmnt);
				StreamResult result = new StreamResult(new File("image.xml"));
				transformer.transform(source, result);
			}
		}
		return image;
	}

	/**
	 * Encode image with Base64 algorithm
	 * 
	 * @return encodedImage
	 * @throws IOException
	 */
	public String encodeImage() throws IOException {
		BufferedImage img = ImageIO.read(new File("Mad.jpg"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "jpg", baos);
		baos.flush();

		String encodedImage = Base64.encodeBase64String(baos.toByteArray());

		baos.close(); // should be inside a finally block

		return encodedImage;
	}

	/**
	 * Decode image with Base64 algorithm
	 * 
	 * @throws Exception
	 */
	public void decodeImage() throws Exception {
		Node image = appendXML();

		String encodedMad = image.getTextContent();
		byte[] bytes = Base64.decodeBase64(encodedMad);
		BufferedImage Image = ImageIO.read(new ByteArrayInputStream(bytes));

		File outputfile = new File("Mad-copy.jpg");
		ImageIO.write(Image, "jpg", outputfile);
	}

	/**
	 * ImageXML for testing
	 * 
	 * @param argv
	 * @throws Exception
	 */
	public static void main(String argv[]) throws Exception {

		ImageXML imageXML = new ImageXML();

		/* 이미지 파일을 인코딩합니다. */
		imageXML.encodeImage();

		/* 이미지 파일을 디코딩합니다. */
		imageXML.decodeImage();
	}
}
