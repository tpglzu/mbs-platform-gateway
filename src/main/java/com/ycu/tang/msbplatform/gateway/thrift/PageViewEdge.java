/**
 * Autogenerated by Thrift Compiler (0.14.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.ycu.tang.msbplatform.gateway.thrift;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.14.2)", date = "2021-09-02")
public class PageViewEdge implements org.apache.thrift.TBase<PageViewEdge, PageViewEdge._Fields>, java.io.Serializable, Cloneable, Comparable<PageViewEdge> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("PageViewEdge");

  private static final org.apache.thrift.protocol.TField PERSON_FIELD_DESC = new org.apache.thrift.protocol.TField("person", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField PAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("page", org.apache.thrift.protocol.TType.STRUCT, (short)2);
  private static final org.apache.thrift.protocol.TField NONCE_FIELD_DESC = new org.apache.thrift.protocol.TField("nonce", org.apache.thrift.protocol.TType.I64, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new PageViewEdgeStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new PageViewEdgeTupleSchemeFactory();

  public @org.apache.thrift.annotation.Nullable com.ycu.tang.msbplatform.gateway.thrift.PersonID person; // required
  public @org.apache.thrift.annotation.Nullable com.ycu.tang.msbplatform.gateway.thrift.PageID page; // required
  public long nonce; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    PERSON((short)1, "person"),
    PAGE((short)2, "page"),
    NONCE((short)3, "nonce");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // PERSON
          return PERSON;
        case 2: // PAGE
          return PAGE;
        case 3: // NONCE
          return NONCE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __NONCE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.PERSON, new org.apache.thrift.meta_data.FieldMetaData("person", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.ycu.tang.msbplatform.gateway.thrift.PersonID.class)));
    tmpMap.put(_Fields.PAGE, new org.apache.thrift.meta_data.FieldMetaData("page", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.ycu.tang.msbplatform.gateway.thrift.PageID.class)));
    tmpMap.put(_Fields.NONCE, new org.apache.thrift.meta_data.FieldMetaData("nonce", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(PageViewEdge.class, metaDataMap);
  }

  public PageViewEdge() {
  }

  public PageViewEdge(
    com.ycu.tang.msbplatform.gateway.thrift.PersonID person,
    com.ycu.tang.msbplatform.gateway.thrift.PageID page,
    long nonce)
  {
    this();
    this.person = person;
    this.page = page;
    this.nonce = nonce;
    setNonceIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public PageViewEdge(PageViewEdge other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetPerson()) {
      this.person = new com.ycu.tang.msbplatform.gateway.thrift.PersonID(other.person);
    }
    if (other.isSetPage()) {
      this.page = new com.ycu.tang.msbplatform.gateway.thrift.PageID(other.page);
    }
    this.nonce = other.nonce;
  }

  public PageViewEdge deepCopy() {
    return new PageViewEdge(this);
  }

  @Override
  public void clear() {
    this.person = null;
    this.page = null;
    setNonceIsSet(false);
    this.nonce = 0;
  }

  @org.apache.thrift.annotation.Nullable
  public com.ycu.tang.msbplatform.gateway.thrift.PersonID getPerson() {
    return this.person;
  }

  public PageViewEdge setPerson(@org.apache.thrift.annotation.Nullable com.ycu.tang.msbplatform.gateway.thrift.PersonID person) {
    this.person = person;
    return this;
  }

  public void unsetPerson() {
    this.person = null;
  }

  /** Returns true if field person is set (has been assigned a value) and false otherwise */
  public boolean isSetPerson() {
    return this.person != null;
  }

  public void setPersonIsSet(boolean value) {
    if (!value) {
      this.person = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public com.ycu.tang.msbplatform.gateway.thrift.PageID getPage() {
    return this.page;
  }

  public PageViewEdge setPage(@org.apache.thrift.annotation.Nullable com.ycu.tang.msbplatform.gateway.thrift.PageID page) {
    this.page = page;
    return this;
  }

  public void unsetPage() {
    this.page = null;
  }

  /** Returns true if field page is set (has been assigned a value) and false otherwise */
  public boolean isSetPage() {
    return this.page != null;
  }

  public void setPageIsSet(boolean value) {
    if (!value) {
      this.page = null;
    }
  }

  public long getNonce() {
    return this.nonce;
  }

  public PageViewEdge setNonce(long nonce) {
    this.nonce = nonce;
    setNonceIsSet(true);
    return this;
  }

  public void unsetNonce() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __NONCE_ISSET_ID);
  }

  /** Returns true if field nonce is set (has been assigned a value) and false otherwise */
  public boolean isSetNonce() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __NONCE_ISSET_ID);
  }

  public void setNonceIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __NONCE_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case PERSON:
      if (value == null) {
        unsetPerson();
      } else {
        setPerson((com.ycu.tang.msbplatform.gateway.thrift.PersonID)value);
      }
      break;

    case PAGE:
      if (value == null) {
        unsetPage();
      } else {
        setPage((com.ycu.tang.msbplatform.gateway.thrift.PageID)value);
      }
      break;

    case NONCE:
      if (value == null) {
        unsetNonce();
      } else {
        setNonce((java.lang.Long)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case PERSON:
      return getPerson();

    case PAGE:
      return getPage();

    case NONCE:
      return getNonce();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case PERSON:
      return isSetPerson();
    case PAGE:
      return isSetPage();
    case NONCE:
      return isSetNonce();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof PageViewEdge)
      return this.equals((PageViewEdge)that);
    return false;
  }

  public boolean equals(PageViewEdge that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_person = true && this.isSetPerson();
    boolean that_present_person = true && that.isSetPerson();
    if (this_present_person || that_present_person) {
      if (!(this_present_person && that_present_person))
        return false;
      if (!this.person.equals(that.person))
        return false;
    }

    boolean this_present_page = true && this.isSetPage();
    boolean that_present_page = true && that.isSetPage();
    if (this_present_page || that_present_page) {
      if (!(this_present_page && that_present_page))
        return false;
      if (!this.page.equals(that.page))
        return false;
    }

    boolean this_present_nonce = true;
    boolean that_present_nonce = true;
    if (this_present_nonce || that_present_nonce) {
      if (!(this_present_nonce && that_present_nonce))
        return false;
      if (this.nonce != that.nonce)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetPerson()) ? 131071 : 524287);
    if (isSetPerson())
      hashCode = hashCode * 8191 + person.hashCode();

    hashCode = hashCode * 8191 + ((isSetPage()) ? 131071 : 524287);
    if (isSetPage())
      hashCode = hashCode * 8191 + page.hashCode();

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(nonce);

    return hashCode;
  }

  @Override
  public int compareTo(PageViewEdge other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetPerson(), other.isSetPerson());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPerson()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.person, other.person);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetPage(), other.isSetPage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.page, other.page);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetNonce(), other.isSetNonce());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNonce()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.nonce, other.nonce);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("PageViewEdge(");
    boolean first = true;

    sb.append("person:");
    if (this.person == null) {
      sb.append("null");
    } else {
      sb.append(this.person);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("page:");
    if (this.page == null) {
      sb.append("null");
    } else {
      sb.append(this.page);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("nonce:");
    sb.append(this.nonce);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (person == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'person' was not present! Struct: " + toString());
    }
    if (page == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'page' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'nonce' because it's a primitive and you chose the non-beans generator.
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class PageViewEdgeStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public PageViewEdgeStandardScheme getScheme() {
      return new PageViewEdgeStandardScheme();
    }
  }

  private static class PageViewEdgeStandardScheme extends org.apache.thrift.scheme.StandardScheme<PageViewEdge> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, PageViewEdge struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // PERSON
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.person = new com.ycu.tang.msbplatform.gateway.thrift.PersonID();
              struct.person.read(iprot);
              struct.setPersonIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // PAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.page = new com.ycu.tang.msbplatform.gateway.thrift.PageID();
              struct.page.read(iprot);
              struct.setPageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // NONCE
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.nonce = iprot.readI64();
              struct.setNonceIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      if (!struct.isSetNonce()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'nonce' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, PageViewEdge struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.person != null) {
        oprot.writeFieldBegin(PERSON_FIELD_DESC);
        struct.person.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.page != null) {
        oprot.writeFieldBegin(PAGE_FIELD_DESC);
        struct.page.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(NONCE_FIELD_DESC);
      oprot.writeI64(struct.nonce);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class PageViewEdgeTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public PageViewEdgeTupleScheme getScheme() {
      return new PageViewEdgeTupleScheme();
    }
  }

  private static class PageViewEdgeTupleScheme extends org.apache.thrift.scheme.TupleScheme<PageViewEdge> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, PageViewEdge struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.person.write(oprot);
      struct.page.write(oprot);
      oprot.writeI64(struct.nonce);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, PageViewEdge struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.person = new com.ycu.tang.msbplatform.gateway.thrift.PersonID();
      struct.person.read(iprot);
      struct.setPersonIsSet(true);
      struct.page = new com.ycu.tang.msbplatform.gateway.thrift.PageID();
      struct.page.read(iprot);
      struct.setPageIsSet(true);
      struct.nonce = iprot.readI64();
      struct.setNonceIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

