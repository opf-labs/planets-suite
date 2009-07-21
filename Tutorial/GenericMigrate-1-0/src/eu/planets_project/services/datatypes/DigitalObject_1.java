package eu.planets_project.services.datatypes;

import java.lang.IllegalArgumentException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import eu.planets_project.services.migrate.Migrate_1;

/**
 * Representation of a concrete digital object, to be passed through web
 * services. Similar to the other PLANETS data types, it is designed as a
 * JAVA Bean.<p/>
 * This class is immutable in practice; its instances can therefore be shared
 * freely and concurrently. Instances are created using static factory methods.
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(namespace=Migrate_1.sNS)
public final class DigitalObject_1
{
  /** actual content (mandatory) */
  @XmlElement(name="content")
  private Content_1 m_content;
  public Content_1 getContent() { return m_content; }
  private void setContent(Content_1 content) { m_content = content; }
  
  /** title */ 
  @XmlElement(name="sTitle")
  private String m_sTitle;
  public String getTitle() {return m_sTitle; }
  private void setTitle(String sTitle) { m_sTitle = sTitle; }
  
  /** format */
  @XmlElement(name="uriFormat")
  private URI m_uriFormat;
  public URI getFormat() { return m_uriFormat; }
  private void setFormat(URI uriFormat) { m_uriFormat = uriFormat; }
  
  /** additional repository-specific metadata */
  @XmlElement(name="listMetadata")
  private List<Metadata_1> m_listMetadata;
  public List<Metadata_1> getMetadata() { return new ArrayList<Metadata_1>(m_listMetadata); }
  private void setMetadata(List<Metadata_1> listMetadata)
  {
    if (listMetadata != null)
      m_listMetadata = new ArrayList<Metadata_1>(listMetadata);
  }
  
  /* make constructor inaccessible */
  private DigitalObject_1() {}
  
  /** factory */
  public static DigitalObject_1 getInstance(
      String sTitle,
      URI uriFormat,
      Content_1 content,
      List<Metadata_1>listMetadata)
  {
    if (content == null)
      throw new IllegalArgumentException("DigitalObject cannot be instantiated without Content!");
    DigitalObject_1 d = new DigitalObject_1();
    d.setTitle(sTitle);
    d.setFormat(uriFormat);
    d.setContent(content);
    d.setMetadata(listMetadata);
    return d;
  } /* getInstance */
  
} /* class DigitalObject_1 */
