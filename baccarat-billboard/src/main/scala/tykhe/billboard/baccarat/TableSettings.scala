package tykhe.billboard.baccarat

case class TableSettings(
  tableId: String,
  handBetMin: String,
  handBetMax: String,
  tieBetMin: String,
  tieBetMax: String,
  pairBetMin: String,
  pairBetMax: String,
  superBetMin: String,
  superBetMax: String,
  language: String,
  theme: String)


