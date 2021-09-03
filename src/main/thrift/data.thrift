namespace java com.ycu.tang.msbplatform.gateway.thrift

include "properties.thrift"
include "edges.thrift"

union DataUnit {
  1: properties.PersonProperty person_property;
  2: properties.PageProperty page_property;
  3: edges.EquivEdge equiv;
  4: edges.PageViewEdge page_view;
}

struct Pedigree {
  1: required i32 true_as_of_secs;
}

struct Data {
  1: required Pedigree pedigree;
  2: required DataUnit dataunit;
}