import grails.converters.JSON
import org.apache.log4j.Logger

/**
 * Usage : new JSONSerializer().getJSON(<dataObject>);
 * Created by : Anubhav Shukla
 * Date: 5/25/2013
 */
class JSONSerializer {

    Logger log = Logger.getInstance(getClass());

    /**
     * List of fields that should be excluded from json response. Customize as per your need.
     */
    def excludedFieldsArr = [
            'dbo',
            'id'
    ]

    /**
     * Checks if an object can directly be serialized.
     * @param propValue
     * @return
     */
    private boolean isSimple(propValue) {
        propValue instanceof Serializable || propValue == null
    }

    /**
     * This is the main method and should be called by outside classes to obtain json.
     * @param object
     * @return
     */
    def getJSON(def object) {
        Map keyMap = convertObj(object);
        return [object: keyMap] as JSON
    }

    /**
     * Recursively converts the data object into map of key-values.
     *
     * @param object
     * @return
     */
    def convertObj(def object) {
        Map keyMap = new HashMap();
        object.properties.each { propName, propValue ->
            if (!excludedFieldsArr.contains(propName)) {
                if (propValue != null) {
                    if (isSimple(propValue)) {
                        keyMap.put(propName, propValue);
                    } else if (propValue instanceof Collection) {   //handling list etc.
                        List colList = new ArrayList();
                        propValue.each { colObj ->
                            if (isSimple(colObj)) {
                                colList.add(colObj)
                            } else {
                                colList.add(convertObj(colObj));
                            }
                        }
                        keyMap.put(propName, colList)
                    } else {
                        //if some other kind of data object the converting that to key-value map.
                        keyMap.put(propName, convertObj(propValue))
                    }
                }
            }
        }
        return keyMap;
    }
}
