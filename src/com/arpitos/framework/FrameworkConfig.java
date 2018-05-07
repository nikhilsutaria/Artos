package com.arpitos.framework;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is responsible for storing framework Configuration. During test
 * suit execution XML file will be searched at location ./conf
 * 
 * @author ArpitS
 *
 */
public class FrameworkConfig {

	final File fXmlFile = new File("./conf/Framework_Config.xml");

	// Organisation Info
	private String Organisation_Name = "Organisation_Name";
	private String Organisation_Address = "XX, Test Street, Test address";
	private String Organisation_Country = "NewZealand";
	private String Organisation_Contact_Number = "+64 1234567";
	private String Organisation_Email = "test@gmail.com";
	private String Organisation_Website = "www.arpitos.com";

	// Logger
	private String logLevel = "debug";
	private String logRootDir = "./reporting/";
	private boolean enableLogDecoration = false;
	private boolean enableTextLog = true;
	private boolean enableHTMLLog = false;

	// Features
	private boolean enableGUITestSelector = true;

	public FrameworkConfig(boolean createIfNotPresent) {
		readXMLConfig(createIfNotPresent);
	}

	/**
	 * Reads XML file from project root location, If not found then default info
	 * is applied
	 * 
	 * @throws Exception
	 */
	public void readXMLConfig(boolean createIfNotPresent) {

		try {
			if (!fXmlFile.exists() || !fXmlFile.isFile()) {
				if (createIfNotPresent) {
					fXmlFile.getParentFile().mkdirs();
					writeDefaultConfig(fXmlFile);
				}
			}

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			readOrganisationInfo(doc);
			readLoggerConfig(doc);
			readFeatures(doc);
		} catch (FileNotFoundException fe) {
			System.out.println(fe.getMessage() + "\n" + "Fall back to Default Organisation values");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeDefaultConfig(File fXmlFile) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("configuration");
		doc.appendChild(rootElement);

		// Organisation Info elements
		Element orgnization_info = doc.createElement("organization_info");
		rootElement.appendChild(orgnization_info);

		// Properties of Organisation Info
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Name()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Name");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Address()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Address");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Country()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Country");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Contact_Number()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Contact_Number");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Email()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Email");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Website()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Website");
			property.setAttributeNode(attr);
		}

		// Logger config elements
		Element logger = doc.createElement("logger");
		rootElement.appendChild(logger);
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getLogLevel()));
			logger.appendChild(property);

			Comment comment = doc.createComment("LogLevel Options : info:debug:trace:fatal:warn:all");
			property.getParentNode().insertBefore(comment, property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("logLevel");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getLogRootDir()));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("logRootDir");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableLogDecoration())));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableLogDecoration");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableTextLog())));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableTextLog");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableHTMLLog())));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableHTMLLog");
			property.setAttributeNode(attr);
		}

		// Features
		Element features = doc.createElement("features");
		rootElement.appendChild(features);
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableGUITestSelector())));
			features.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableGUITestSelector");
			property.setAttributeNode(attr);
		}
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(fXmlFile);

		transformer.transform(source, result);

	}

	private void readLoggerConfig(Document doc) {
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("logger");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			NodeList nChildList = nNode.getChildNodes();
			for (int i = 0; i < nChildList.getLength(); i++) {
				Node nChildNode = nChildList.item(i);
				if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nChildNode;
					// System.out.println(eElement.getNodeName());
					// System.out.println(eElement.getAttribute("name"));
					// System.out.println(eElement.getAttribute("name")
					// +
					// ":" +
					// eElement.getTextContent());
					if ("logLevel".equals(eElement.getAttribute("name"))) {
						setLogLevel(eElement.getTextContent());
					}
					if ("logRootDir".equals(eElement.getAttribute("name"))) {
						if (eElement.getTextContent().endsWith("/") || eElement.getTextContent().endsWith("\\")) {
							setLogRootDir(eElement.getTextContent());
						} else {
							setLogRootDir(eElement.getTextContent() + File.separator);
						}
					}
					if ("enableLogDecoration".equals(eElement.getAttribute("name"))) {
						setEnableLogDecoration(Boolean.parseBoolean(eElement.getTextContent()));
					}
					if ("enableTextLog".equals(eElement.getAttribute("name"))) {
						setEnableTextLog(Boolean.parseBoolean(eElement.getTextContent()));
					}
					if ("enableHTMLLog".equals(eElement.getAttribute("name"))) {
						setEnableHTMLLog(Boolean.parseBoolean(eElement.getTextContent()));
					}
				}
			}
		}
	}

	private void readFeatures(Document doc) {
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("features");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			NodeList nChildList = nNode.getChildNodes();
			for (int i = 0; i < nChildList.getLength(); i++) {
				Node nChildNode = nChildList.item(i);
				if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nChildNode;
					// System.out.println(eElement.getNodeName());
					// System.out.println(eElement.getAttribute("name"));
					// System.out.println(eElement.getAttribute("name")
					// +
					// ":" +
					// eElement.getTextContent());
					if ("enableGUITestSelector".equals(eElement.getAttribute("name"))) {
						setEnableGUITestSelector(Boolean.parseBoolean(eElement.getTextContent()));
					}
				}
			}
		}
	}

	private void readOrganisationInfo(Document doc) {
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("organization_info");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			NodeList nChildList = nNode.getChildNodes();
			for (int i = 0; i < nChildList.getLength(); i++) {
				Node nChildNode = nChildList.item(i);
				if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nChildNode;
					// System.out.println(eElement.getNodeName());
					// System.out.println(eElement.getAttribute("name"));
					// System.out.println(eElement.getAttribute("name") +
					// ":" +
					// eElement.getTextContent());
					if ("Name".equals(eElement.getAttribute("name"))) {
						setOrganisation_Name(eElement.getTextContent());
					}
					if ("Address".equals(eElement.getAttribute("name"))) {
						setOrganisation_Address(eElement.getTextContent());
					}
					if ("Country".equals(eElement.getAttribute("name"))) {
						setOrganisation_Country(eElement.getTextContent());
					}
					if ("Contact_Number".equals(eElement.getAttribute("name"))) {
						setOrganisation_Contact_Number(eElement.getTextContent());
					}
					if ("Email".equals(eElement.getAttribute("name"))) {
						setOrganisation_Email(eElement.getTextContent());
					}
					if ("Website".equals(eElement.getAttribute("name"))) {
						setOrganisation_Website(eElement.getTextContent());
					}
				}
			}
		}
	}

	public String getOrganisation_Name() {
		return Organisation_Name;
	}

	public void setOrganisation_Name(String organisation_Name) {
		Organisation_Name = organisation_Name;
	}

	public String getOrganisation_Address() {
		return Organisation_Address;
	}

	public void setOrganisation_Address(String organisation_Address) {
		Organisation_Address = organisation_Address;
	}

	public String getOrganisation_Country() {
		return Organisation_Country;
	}

	public void setOrganisation_Country(String organisation_Country) {
		Organisation_Country = organisation_Country;
	}

	public String getOrganisation_Contact_Number() {
		return Organisation_Contact_Number;
	}

	public void setOrganisation_Contact_Number(String organisation_Contact_Number) {
		Organisation_Contact_Number = organisation_Contact_Number;
	}

	public String getOrganisation_Email() {
		return Organisation_Email;
	}

	public void setOrganisation_Email(String organisation_Email) {
		Organisation_Email = organisation_Email;
	}

	public String getOrganisation_Website() {
		return Organisation_Website;
	}

	public void setOrganisation_Website(String organisation_Website) {
		Organisation_Website = organisation_Website;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public boolean isEnableLogDecoration() {
		return enableLogDecoration;
	}

	public void setEnableLogDecoration(boolean enableLogDecoration) {
		this.enableLogDecoration = enableLogDecoration;
	}

	public boolean isEnableTextLog() {
		return enableTextLog;
	}

	public void setEnableTextLog(boolean enableTextLog) {
		this.enableTextLog = enableTextLog;
	}

	public boolean isEnableHTMLLog() {
		return enableHTMLLog;
	}

	public void setEnableHTMLLog(boolean enableHTMLLog) {
		this.enableHTMLLog = enableHTMLLog;
	}

	public String getLogRootDir() {
		return logRootDir;
	}

	public void setLogRootDir(String logRootDir) {
		this.logRootDir = logRootDir;
	}

	public boolean isEnableGUITestSelector() {
		return enableGUITestSelector;
	}

	public void setEnableGUITestSelector(boolean enableGUITestSelector) {
		this.enableGUITestSelector = enableGUITestSelector;
	}
}