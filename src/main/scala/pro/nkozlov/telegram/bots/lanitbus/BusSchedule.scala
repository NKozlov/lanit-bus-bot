package pro.nkozlov.telegram.bots.lanitbus


import com.typesafe.scalalogging.LazyLogging
import org.apache.poi.xssf.usermodel.XSSFWorkbook

/**
  * todo Document type BusSchedule
  */
class LoadExcelSchedule(path: String) extends LazyLogging {

  def loadScheduleFromFile(): Map[String, Map[String, List[List[String]]]] = {
    logger.debug("invoke loadScheduleFromFile, path = {}", path)

    logger.info("start load excel file '{}'", path)

    val myExcelBook = new XSSFWorkbook(getClass.getClassLoader.getResourceAsStream(path))
    logger.debug("loaded excel book")
    val myExcelSheet = myExcelBook.getSheet("Лист1")
    logger.debug("loaded excel sheet = {}", myExcelSheet.getSheetName)

    // settings
    val startRow = 3
    var currRow = startRow
    val lastRowRizhskayaOffice = 35
    val lastRowOfficeRizhskaya = 38
    val startRowMarinaRoszha = 42
    val lastRowOfficeMarinaRoszha = 45
    val lastRow = 47

    // Описание расписания Рижская-Мурманский (первая часть)
    val cellRizhskayaToOfficeType1 = 0
    val cellRizhskayaToOfficeTime1 = 1

    // Описание расписания Мурманский-Рижская (первая часть)
    val cellOfficeToRizhskayaTime1 = 2
    val cellOfficeToRizhskayaType1 = 3

    // Описание расписания Метро-Офис (вторая часть)
    val cellMetroToOfficeType = 5
    val cellMetroToOfficeTime = 6

    // Описание расписания Офис-Метро (вторая часть)
    val cellOfficeToMetroTime = 7
    val cellOfficeToMetroType = 8


    var scheduleRizhskayaOffice: List[List[String]] = Nil
    var scheduleOfficeRizhskaya: List[List[String]] = Nil
    var scheduleMarinaRoszhaOffice: List[List[String]] = Nil
    var scheduleOfficeMarinaRoszha: List[List[String]] = Nil

    def getTimeFromDate(date: java.util.Date): String = new java.text.SimpleDateFormat("HH:mm").format(date)

    while (currRow <= lastRow) {
      logger.debug("handle current row = {}", currRow)
      val row = myExcelSheet.getRow(currRow)
      // Маппинг м. Рижская до офиса (1 столбец)
      scheduleRizhskayaOffice = List(row.getCell(cellRizhskayaToOfficeType1).getStringCellValue,
        getTimeFromDate(row.getCell(cellRizhskayaToOfficeTime1).getDateCellValue)) :: scheduleRizhskayaOffice
      // Маппинг Офис до м. Рижская (1 столбец)
      scheduleOfficeRizhskaya = List(row.getCell(cellOfficeToRizhskayaType1).getStringCellValue,
        getTimeFromDate(row.getCell(cellOfficeToRizhskayaTime1).getDateCellValue)) :: scheduleOfficeRizhskaya

      // Маппинг м. Рижская до офиса (2 столбец)
      if (currRow <= lastRowRizhskayaOffice) scheduleRizhskayaOffice = List(row.getCell(cellMetroToOfficeType).getStringCellValue,
        getTimeFromDate(row.getCell(cellMetroToOfficeTime).getDateCellValue)) :: scheduleRizhskayaOffice
      // Маппинг Офис до м. Рижская (2 столбец)
      if (currRow <= lastRowOfficeRizhskaya) scheduleOfficeRizhskaya = List(row.getCell(cellOfficeToMetroType).getStringCellValue,
        getTimeFromDate(row.getCell(cellOfficeToMetroTime).getDateCellValue)) :: scheduleOfficeRizhskaya

      if (currRow >= startRowMarinaRoszha) {
        // Маппинг м. Марьина Роща до офиса (2 столбец)
        scheduleMarinaRoszhaOffice = List(row.getCell(cellMetroToOfficeType).getStringCellValue,
          getTimeFromDate(row.getCell(cellMetroToOfficeTime).getDateCellValue)) :: scheduleMarinaRoszhaOffice
        // Маппинг Офис до м. Марьина Роща (2 столбец)
        if (currRow <= lastRowOfficeMarinaRoszha) scheduleOfficeMarinaRoszha = List(row.getCell(cellOfficeToMetroType).getStringCellValue,
          getTimeFromDate(row.getCell(cellOfficeToMetroTime).getDateCellValue)) :: scheduleOfficeMarinaRoszha
      }

      currRow += 1
    }

    logger.info("excel file {} load successful", path)

    val scheduleBus = Map("ToOffice" -> Map("FromRizhskaya" -> scheduleRizhskayaOffice, "FromMarinaRoszha" -> scheduleMarinaRoszhaOffice),
      "FromOffice" -> Map("ToRizhskaya" -> scheduleOfficeRizhskaya, "ToMarinaRoszha" -> scheduleOfficeMarinaRoszha))

    logger.debug("scheduleBus = {}", scheduleBus)

    scheduleBus
  }


}

object BusSchedule extends LazyLogging {

  val scheduleBus: Map[String, Map[String, List[List[String]]]] = new LoadExcelSchedule(ConfigContext.srcExcelFile).loadScheduleFromFile()

  // Рижская - Офис
  def scheduleFromRizhskaya(): List[List[String]] = {
    logger.debug("invoke scheduleFromRizhskaya()")
    val scheduleRizhskayaOffice = scheduleBus("ToOffice")("FromRizhskaya").filter(list => list.head contains "М").sortBy(list => list(1))
    logger.debug("scheduleRizhskayaOffice = {}", scheduleRizhskayaOffice)
    scheduleRizhskayaOffice
  }

  // Офис - Рижская
  def scheduleOfficeToRizhskaya(): List[List[String]] = {
    logger.debug("invoke scheduleOfficeToRizhskaya()")
    val scheduleOfficeRizhskaya = scheduleBus("FromOffice")("ToRizhskaya").filter(list => list.head contains "М").sortBy(list => list(1))
    logger.debug("scheduleOfficeRizhskaya = {}", scheduleOfficeRizhskaya)
    scheduleOfficeRizhskaya
  }

  // Марьина Роща - Офис
  def scheduleFromMarinaRoszha(): List[List[String]] = {
    logger.debug("invoke scheduleOfficeToRizhskaya()")
    val scheduleMarinaRoszhaOffice = scheduleBus("ToOffice")("FromMarinaRoszha").filter(list => list.head contains "М").sortBy(list => list(1))
    logger.debug("scheduleMarinaRoszhaOffice = {}", scheduleMarinaRoszhaOffice)
    scheduleMarinaRoszhaOffice
  }

  // Офис - Марьина Роща
  def scheduleOfficeToMarinaRoszha(): List[List[String]] = {
    logger.debug("invoke scheduleOfficeToRizhskaya()")
    val scheduleOfficeMarinaRoszha = scheduleBus("FromOffice")("ToMarinaRoszha").filter(list => list.head contains "М").sortBy(list => list(1))
    logger.debug("scheduleOfficeMarinaRoszha = {}", scheduleOfficeMarinaRoszha)
    scheduleOfficeMarinaRoszha
  }

  // Ближайший транспорт до метро (по текущему времени)
  def scheduleToMetro(date: String): List[List[List[String]]] = {
    logger.debug("invoke scheduleToMetro({})", date)
    val scheduleToRizhskaya = scheduleBus("FromOffice")("ToRizhskaya").filter(list => list(1) > date).sortBy(list => list(1))
    val scheduleToMarinaRoszha = scheduleBus("FromOffice")("ToMarinaRoszha").filter(list => list(1) > date).sortBy(list => list(1))

    List(scheduleToRizhskaya, scheduleToMarinaRoszha)
  }

  // Ближайший транспорт до офиса (по текущему времени)
  def scheduleToOffice(date: String): List[List[List[String]]] = {
    logger.debug("invoke scheduleToMetro({})", date)
    val scheduleFromRizhskaya = scheduleBus("ToOffice")("FromRizhskaya").filter(list => list(1) > date).sortBy(list => list(1))
    val scheduleFromMarinaRoszha = scheduleBus("ToOffice")("FromMarinaRoszha").filter(list => list(1) > date).sortBy(list => list(1))

    List(scheduleFromRizhskaya, scheduleFromMarinaRoszha)
  }
}
