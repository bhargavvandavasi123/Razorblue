package com.pageinfo.controller;
package com.enappsys.data.core.datatype;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.enappsys.data.core.CoreDataConstants;
import com.enappsys.data.core.datareadlayer.IDataReadLayer;
import com.enappsys.data.core.datatype.pojo.DataTypePOJO;
import com.enappsys.data.core.datatype.pojo.vo.DataTypeTimePair;
import com.enappsys.data.core.entityitem.EntityItem;
import com.enappsys.data.core.time.DateTimeType;
import com.enappsys.persistentcollection.list.APersistentListValue;
import com.enappsys.persistentcollection.list.IPersistentListValueObjectFactory;
import com.enappsys.persistentcollection.list.PersistentVector;

import ch.qos.logback.core.joran.sanity.Pair;

public class DataType extends APersistentListValue<DataType> implements Comparable<DataType> {
  private static final String DBFIELD_CODE = "Code";
  private static final String DBFIELD_GUI_CODE = "GUICode";
  private static final String DBFIELD_DESCRIPTION = "Description";
  private static final String DBFIELD_BASE_DTT = "BaseDTT";
  private static final String DBFIELD_DEFAULT_TIME_ZONE = "DefaultTimeZone";
  private static final String DBFIELD_VALUE_TYPE = "ValueType";
  private static final String DBFIELD_HAS_ENTITIES = "HasEntities";
  private static final String DBFIELD_SUMMARISE = "Summarise";
  private static final String DBFIELD_SUMMARY_STATISTICS = "SummaryStatistics";
  private static final String DBFIELD_SCALE = "Scale";
  private static final String DBFIELD_TABLE_NAME = "TableName";
  private static final String DBFIELD_SPOT_OR_PERIOD = "SpotOrPeriod";

  private static final int DEFAULT_SCALE_VALUE = 2;

  public enum VALUE_TYPE {
    NUMERIC("NUMERIC"), STRING("STRING"), BOOLEAN("BOOLEAN");

    private final String s;

    private VALUE_TYPE(final String s) {
      this.s = s;
    }

    public String toString() {
      return this.s;
    }
  };

  private static List<DataType> list = new ArrayList<DataType>();
  private static Map<String, DataType> htByServerCode = new TreeMap<String, DataType>(String.CASE_INSENSITIVE_ORDER);
  private static Map<String, DataType> htByGUICode = new TreeMap<String, DataType>(String.CASE_INSENSITIVE_ORDER);
  private String code;
  private String guiCode;
  private String description;
  private DateTimeType baseResolution;
  private TimeZone defaultTimeZone;
  private VALUE_TYPE valueType;
  private boolean hasEntities;
  private boolean summaryStatistics = false;
  private boolean summarise = true;
  private int scale = DEFAULT_SCALE_VALUE;
  private String tableName = CoreDataConstants.VALUE_TABLE_NAME;
  private String spotOrPeriod = CoreDataConstants.PERIOD;

  private static final ConcurrentHashMap<String, DataType> cache = new ConcurrentHashMap<String, DataType>();

  private static ZonedDateTime timestamp = ZonedDateTime.now();

  protected DataType() {}

  private int daysMinus;
  private int daysPlus;

  protected DataType(final String code, final String description, final String guiCode,
      final DateTimeType baseResolution, final TimeZone defaultTimeZone, final VALUE_TYPE valueType,
      final boolean hasEntities, final boolean summaryStatistics, final boolean summarise, final int scale,
      final String tableName, final String spotOrPeriod) {
    this.code = code;
    this.description = description;
    this.guiCode = guiCode;
    this.baseResolution = baseResolution;
    this.defaultTimeZone = defaultTimeZone;
    this.valueType = valueType;
    this.hasEntities = hasEntities;
    this.summaryStatistics = summaryStatistics;
    this.summarise = summarise;
    this.scale = scale;
    this.tableName = tableName;
    this.spotOrPeriod = spotOrPeriod;
    if (!list.contains(this)) {
      list.add(this);
    }
    htByServerCode.put(code, this);
    htByGUICode.put(guiCode, this);
    setListName("DataType");

  }

  protected DataType(final String code, final String description, final String guiCode,
      final DateTimeType baseResolution, final TimeZone defaultTimeZone, final VALUE_TYPE valueType,
      final boolean hasEntities) {
    this(code, description, guiCode, baseResolution, defaultTimeZone, valueType, hasEntities, DEFAULT_SCALE_VALUE);
  }

  protected DataType(final String code, final String description, final String guiCode,
      final DateTimeType baseResolution, final TimeZone defaultTimeZone, final VALUE_TYPE valueType,
      final boolean hasEntities, final int scale) {
    this(code, description, guiCode, baseResolution, defaultTimeZone, valueType, hasEntities, false, true, scale,
        CoreDataConstants.VALUE_TABLE_NAME, CoreDataConstants.PERIOD);
  }

  /*
   * This is for the new platform where we would like to use enprompt data that doesn't exist
   * as real entities.  These are not to be stored in any of the tables like EntityInformation or List
   * and are not stored in the internal Maps
   */
  public static DataType createFakeDataType(String code) {
	  DataType retval = new DataType();
	  retval.code = code;
	  return retval;
  }
  

  /**
   * Get the code.
   * 
   * @return the code.
   */
  public String getCode() {
    return this.code;
  }

  /**
   * Get the description.
   * 
   * @return the description.
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Get the guiCode.
   * 
   * @return the guiCode.
   */
  public String getGUICode() {
    return this.guiCode;
  }

  /**
   * Get the base resolution.
   * 
   * @return the base resolution.
   */
  public DateTimeType getBaseResolution() {
    return this.baseResolution;
  }

  /**
   * Get the default time zone.
   * 
   * @return the default time zone.
   */
  public TimeZone getDefaultTimeZone() {
    return this.defaultTimeZone;
  }

  /**
   * Get the best available resolution.
   * 
   * @return the best available resolution.
   */
  public DateTimeType getBestAvailableResolution(final DateTimeType requestedDateTimeType) {
    return getBestAvailableResolution(requestedDateTimeType, null);
  }

  /**
   * Get the best available resolution for entity item
   * 
   * @param requestedDateTimeType
   * @param entityItem
   * @return the best available resolution.
   */
  public DateTimeType getBestAvailableResolution(final DateTimeType requestedDateTimeType,
      final EntityItem entityItem) {
    DateTimeType baseDTT = this.baseResolution;
    if (null != entityItem && null != entityItem.getBaseDTT()) {
      baseDTT = entityItem.getBaseDTT();
    }
    if (baseDTT.compareTo(requestedDateTimeType) > 0) {
      return baseDTT;
    }
    return requestedDateTimeType;
  }

  /**
   * Get the value type.
   * 
   * @return the value type.
   */
  public VALUE_TYPE getValueType() {
    return valueType;
  }

  /**
   * Returns if has entities.
   * 
   * @return <code>true</code> if if has entities.
   */
  public boolean hasEntities() {
    return this.hasEntities;
  }

  /**
   * Returns if has summary statistics
   * 
   * @param summaryStatistics
   */
  public boolean hasSummaryStatistics() {
    return this.summaryStatistics;
  }

  /**
   * Returns if has summarise
   * 
   * @return
   */
  public boolean hasSummarise() {
    return this.summarise;
  }

  /**
   * Get the scale
   * 
   * @return
   */
  public int getScale() {
    return this.scale;
  }

  /**
   * Get the table name.
   * 
   * @return the table name.
   */
  public String getTableName() {
    return this.tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getSpotOrPeriod() {
    return spotOrPeriod;
  }

  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(final DataType o) {
    return this.code.compareTo(o.code);
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return this.code.hashCode();
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.code;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (null == obj) {
      return false;
    } else if (!(obj instanceof DataType)) {
      return false;
    }
    final DataType instance = (DataType) obj;
    return this.code.equals(instance.code);
  }

  /**
   * Get the data type object by the passed server code.
   * 
   * @param code the server code.
   * @return the data type object.
   */
  public static DataType getByServerCode(final String code) {
    final DataType dataType = htByServerCode.get(code);
    if (null == dataType) {
      Log.error("Requesting a DataType object with the server code " + code + " returns null");
    }
    return dataType;
  }

  /**
   * Get the data type object by the passed GUI code.
   * 
   * @param code the GUI code.
   * @return the data type object.
   */
  public static DataType getByGUICode(final String code) {
    final DataType dataType = htByGUICode.get(code);
    if (null == dataType) {
      Log.error("Requesting a DataType object with the GUI code " + code + " returns null");
    }
    return dataType;
  }

  public static List<DataType> get() {
    return list;
  }

  public static DataType getByDataGroup(final String dataGroup) {
    ZonedDateTime now = ZonedDateTime.now();
    if (now.getHour() - timestamp.getHour() > 3) {
      timestamp = now;
      if (!cache.isEmpty()) {
        cache.clear();
      }
    }
    DataType finalPossible = cache.get(dataGroup);
    if (finalPossible != null) {
      return finalPossible;
    } else {
      final List<DataType> possibles = new ArrayList<DataType>();
      for (int i = 0; i < list.size(); i++) {
        final DataType dataType = list.get(i);
        final String dataTypeCode = dataType.getCode();
        if (dataGroup.startsWith(dataTypeCode)) {
          possibles.add(dataType);
        }
      }
      int length = 0;
      for (DataType possible : possibles) {
        if (possible.getCode().length() > length) {
          finalPossible = possible;
          length = possible.getCode().length();
        }
      }
      cache.put(dataGroup, finalPossible);
      return finalPossible;
    }
  }

  /**
   * @see com.enappsys.persistentcollection.map.APersistentMapValue#populate(com.amazonaws.services.dynamodbv2.document.Item)
   */
  @Override
  protected DataType populate(final Item dbItem) {
    super.populate(dbItem);
    this.code = dbItem.getString(DBFIELD_CODE);
    this.guiCode = dbItem.getString(DBFIELD_GUI_CODE);
    this.description = dbItem.getString(DBFIELD_DESCRIPTION);
    final String brCode = dbItem.getString(DBFIELD_BASE_DTT);
    this.baseResolution = DateTimeType.get(brCode);
    final String tzCode = dbItem.getString(DBFIELD_DEFAULT_TIME_ZONE);
    this.defaultTimeZone = TimeZone.get(tzCode);
    final String valueTypeCode = dbItem.getString(DBFIELD_VALUE_TYPE);
    if (VALUE_TYPE.NUMERIC.toString().equals(valueTypeCode)) {
      this.valueType = VALUE_TYPE.NUMERIC;
    } else if (VALUE_TYPE.BOOLEAN.toString().equals(valueTypeCode)) {
      this.valueType = VALUE_TYPE.BOOLEAN;
    } else if (VALUE_TYPE.STRING.toString().equals(valueTypeCode)) {
      this.valueType = VALUE_TYPE.STRING;
    }
    this.hasEntities = dbItem.getBoolean(DBFIELD_HAS_ENTITIES);
    if (dbItem.hasAttribute(DBFIELD_SUMMARISE)) {
      this.summarise = dbItem.getBoolean(DBFIELD_SUMMARISE);
    }
    if (dbItem.hasAttribute(DBFIELD_SUMMARY_STATISTICS)) {
      this.summarise = dbItem.getBoolean(DBFIELD_SUMMARISE);
    }
    if (dbItem.hasAttribute(DBFIELD_SCALE)) {
      this.scale = dbItem.getInt(DBFIELD_SCALE);
    }
    if (dbItem.hasAttribute(DBFIELD_TABLE_NAME)) {
      this.tableName = dbItem.getString(DBFIELD_TABLE_NAME);
    }
    if (dbItem.hasAttribute(DBFIELD_SPOT_OR_PERIOD)) {
      this.spotOrPeriod = dbItem.getString(DBFIELD_SPOT_OR_PERIOD);
    }

    Pair pair = DataTypeTimePair.getPair(code);
    if (pair != null) {
      this.daysMinus = pair.getL();
      this.daysPlus = pair.getR();
    } else {
      this.daysMinus = 0;
      this.daysPlus = 0;
    }

    return this;
  }

  /**
   * @see com.enappsys.persistentcollection.map.APersistentMapValue#getDBItem()
   */
  @Override
  protected Item getDBItem() {
    final Item item = super.getDBItem();
    if (null != this.code) {
      item.withString(DBFIELD_CODE, this.code);
    }
    if (null != this.guiCode) {
      item.withString(DBFIELD_GUI_CODE, this.guiCode);
    }
    if (null != this.description) {
      item.withString(DBFIELD_DESCRIPTION, this.description);
    }
    if (null != this.baseResolution) {
      item.withString(DBFIELD_BASE_DTT, this.baseResolution.getCode());
    }
    if (null != this.defaultTimeZone) {
      item.withString(DBFIELD_DEFAULT_TIME_ZONE, this.defaultTimeZone.getCode());
    }
    if (null != this.valueType) {
      item.withString(DBFIELD_VALUE_TYPE, this.valueType.toString());
    }
    item.withBoolean(DBFIELD_HAS_ENTITIES, this.hasEntities);
    item.withBoolean(DBFIELD_SUMMARISE, this.summarise);
    item.withBoolean(DBFIELD_SUMMARY_STATISTICS, this.summaryStatistics);
    item.withInt(DBFIELD_SCALE, this.scale);
    if (null != this.tableName) {
      item.withString(DBFIELD_TABLE_NAME, this.tableName);
    }
    if (null != this.spotOrPeriod) {
      item.withString(DBFIELD_SPOT_OR_PERIOD, this.spotOrPeriod);
    }
    return item;
  }

  public static IPersistentListValueObjectFactory<DataType> createObjectFactory() {
    return new IPersistentListValueObjectFactory<DataType>() {
      /**
       * @see com.enappsys.persistentcollection.list.IPersistentListValueObjectFactory#create()
       */
      @Override
      public DataType create() {
        return new DataType();
      }
    };
  }

  public static void populate(final AWSCredentialsProvider credentialsProvider) {
    final PersistentVector<DataType> dbData = new PersistentVector<DataType>(createObjectFactory(), credentialsProvider,
        "DataType", true, TimeUnit.MINUTES.toSeconds(5));

    final Hashtable<String, DataType> localServerHT = new Hashtable<String, DataType>();
    final Hashtable<String, DataType> localGUIHT = new Hashtable<String, DataType>();
    final List<DataType> localList = new Vector<DataType>();
    for (DataType item : dbData) {
      localList.add(item);
      localServerHT.put(item.code, item);
      localGUIHT.put(item.guiCode, item);
    }
    // Not thread safe
    list.clear();
    list.addAll(localList);
    htByServerCode.clear();
    htByServerCode.putAll(localServerHT);
    htByGUICode.clear();
    htByGUICode.putAll(localGUIHT);
  }

  public DataTypePOJO createPOJO() {
    final DataTypePOJO retVal = new DataTypePOJO();
    retVal.setCode(this.code);
    retVal.setGuiCode(this.guiCode);
    retVal.setDescription(this.description);
    retVal.setBaseResolution(this.baseResolution.getCode());
    retVal.setDefaultTimeZone(this.defaultTimeZone.getCode());
    retVal.setValueType(this.valueType.toString());
    retVal.setHasEntities(this.hasEntities);
    retVal.setSummarise(this.summarise);
    retVal.setSummaryStatistics(this.summaryStatistics);
    retVal.setScale(this.scale);
    retVal.setTableName(this.tableName);
    retVal.setSpotOrPeriod(this.spotOrPeriod);
    return retVal;
  }

  /**
   * Get the list of entities
   * 
   * @param dataReadLayer
   * @param code - server DataType code
   * @return entities - the list of entities
   * @throws Exception
   */
  public static List<String> getEntitiesListByServerCode(final IDataReadLayer dataReadLayer, final String code)
      throws Exception {
    final ArrayList<String> entities = new ArrayList<>();
    for (final EntityItem entityItem : dataReadLayer.getEntityInformation(getByServerCode(code))) {
      for (final String entity : entityItem.getEntities()) {
        entities.add(entity);
      }
    }
    return entities;
  }

  /**
   * Get the list of compound entities
   * 
   * @param dataReadLayer
   * @param code - server DataType code
   * @return entities - the list of entities
   * @throws Exception
   */
  public static List<String> getCompoundEntitiesListByServerCode(final IDataReadLayer dataReadLayer, final String code)
      throws Exception {
    final ArrayList<String> entities = new ArrayList<>();
    for (final EntityItem entityItem : dataReadLayer.getEntityInformation(getByServerCode(code))) {
      entities.add(entityItem.getCompoundEntities(entityItem.getEntities()));
    }
    return entities;
  }

  public int getDaysMinus() {
    return daysMinus;
  }

  public void setDaysMinus(int daysMinus) {
    this.daysMinus = daysMinus;
  }

  public int getDaysPlus() {
    return daysPlus;
  }

  public void setDaysPlus(int daysPlus) {
    this.daysPlus = daysPlus;
  }
}