package jira4s.datas

// https://developer.atlassian.com/cloud/jira/platform/rest/#error-responses

/*
{
  "id": "https://docs.atlassian.com/jira/REST/schema/error-collection#",
  "title": "Error Collection",
  "type": "object",
  "properties": {
    "errorMessages": {
      "type": "array",
      "items": {
        "type": "string"
      }
    },
    "errors": {
      "type": "object",
      "patternProperties": {
        ".+": {
          "type": "string"
        }
      },
      "additionalProperties": false
    },
    "status": {
      "type": "integer"
    }
  },
  "additionalProperties": false
}
 */

case class ApiErrors(
  errorMessages: Seq[String],
  errors: Map[String, String]
)
