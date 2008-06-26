
package eu.planets_project.ifr.core.wdt.common.services.magicTiff2Jpeg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for imageData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="imageData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="original-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="original-bitdepth" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="result" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="converted-data" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "imageData", propOrder = {
    "originalType",
    "originalBitdepth",
    "result",
    "convertedData"
})
public class ImageData {

    @XmlElement(name = "original-type")
    protected String originalType;
    @XmlElement(name = "original-bitdepth")
    protected int originalBitdepth;
    protected boolean result;
    @XmlElement(name = "converted-data")
    protected byte[] convertedData;

    /**
     * Gets the value of the originalType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginalType() {
        return originalType;
    }

    /**
     * Sets the value of the originalType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginalType(String value) {
        this.originalType = value;
    }

    /**
     * Gets the value of the originalBitdepth property.
     * 
     */
    public int getOriginalBitdepth() {
        return originalBitdepth;
    }

    /**
     * Sets the value of the originalBitdepth property.
     * 
     */
    public void setOriginalBitdepth(int value) {
        this.originalBitdepth = value;
    }

    /**
     * Gets the value of the result property.
     * 
     */
    public boolean isResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     */
    public void setResult(boolean value) {
        this.result = value;
    }

    /**
     * Gets the value of the convertedData property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getConvertedData() {
        return convertedData;
    }

    /**
     * Sets the value of the convertedData property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setConvertedData(byte[] value) {
        this.convertedData = ((byte[]) value);
    }

}
