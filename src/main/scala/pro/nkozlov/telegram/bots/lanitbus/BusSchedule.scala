package pro.nkozlov.telegram.bots.lanitbus


import java.io.FileInputStream

import com.typesafe.scalalogging.LazyLogging
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class LoadExcelSchedule(configContext: ConfigContext = ConfigContextImpl) extends LazyLogging {
  val fileName: String = configContext.srcExcelFile
  val folder: String = configContext.scheduleFolder

  def loadScheduleFromFile(): Map[String, Map[String, List[List[String]]]] = {
    logger.debug("invoke loadScheduleFromFile, folder = {}, name = {}", folder, fileName)

    logger.info("start load excel file '{}' (from '{}')", fileName, new java.io.File(".").getCanonicalPath)

    val myExcelBook = new XSSFWorkbook(new FileInputStream(folder + fileName))
    logger.debug("loaded excel book")
    val myExcelSheet = myExcelBook.getSheet("Лист1")
    logger.debug("loaded excel sheet = {}", myExcelSheet.getSheetName)

    // settings
    val startRow = 2
    var currRow = startRow
    val lastRowRizhskayaOffice = 44
    val lastRowOfficeRizhskaya = 46
    val startRowMarinaRoszha = 48
    val lastRowOfficeMarinaRoszha = 51
    val lastRowMarinaRoszhaOffice = 53
    val lastRow = lastRowMarinaRoszhaOffice

    // Рижская-Мурманский
    val cellRizhskayaToOfficeType1 = 1
    val cellRizhskayaToOfficeTime1 = 2
    val cellRizhskayaToOfficeType2 = 3
    val cellRizhskayaToOfficeTime2 = 4

    // Мурманский-Рижская
    val cellOfficeToRizhskayaType1 = 6
    val cellOfficeToRizhskayaTime1 = 7
    val cellOfficeToRizhskayaType2 = 8
    val cellOfficeToRizhskayaTime2 = 9

    // Марьина Роща-Мурманский
    val cellMarinaRoszhaOfficeType = 1
    val cellMarinaRoszhaOfficeTime = 2

    // Мурманский-Марьина Роща
    val cellOfficeMarinaRoszhaType = 3
    val cellOfficeMarinaRoszhaTime = 4


    var scheduleRizhskayaOffice: List[List[String]] = Nil
    var scheduleOfficeRizhskaya: List[List[String]] = Nil
    var scheduleMarinaRoszhaOffice: List[List[String]] = Nil
    var scheduleOfficeMarinaRoszha: List[List[String]] = Nil

    def getTimeFromDate(date: java.util.Date): String = new java.text.SimpleDateFormat("HH:mm").format(date)

    while (currRow <= lastRow) {
      logger.debug("handle current row = {}", currRow)
      val row = myExcelSheet.getRow(currRow)
      // Маппинг Рижская-Мурманский (1 столбец)
      if (currRow <= lastRowRizhskayaOffice) {
        scheduleRizhskayaOffice = List(row.getCell(cellRizhskayaToOfficeType1).getStringCellValue,
          getTimeFromDate(row.getCell(cellRizhskayaToOfficeTime1).getDateCellValue)) :: scheduleRizhskayaOffice
        scheduleRizhskayaOffice = List(row.getCell(cellRizhskayaToOfficeType2).getStringCellValue,
          getTimeFromDate(row.getCell(cellRizhskayaToOfficeTime2).getDateCellValue)) :: scheduleRizhskayaOffice
      }
      // Маппинг Мурманский-Рижская
      if (currRow < lastRowOfficeRizhskaya) {
        scheduleOfficeRizhskaya = List(row.getCell(cellOfficeToRizhskayaType1).getStringCellValue,
          getTimeFromDate(row.getCell(cellOfficeToRizhskayaTime1).getDateCellValue)) :: scheduleOfficeRizhskaya
        scheduleOfficeRizhskaya = List(row.getCell(cellOfficeToRizhskayaType2).getStringCellValue,
          getTimeFromDate(row.getCell(cellOfficeToRizhskayaTime2).getDateCellValue)) :: scheduleOfficeRizhskaya
      } else if (currRow == lastRowOfficeRizhskaya) {
        // Мурманский-Рижская -- не заполнены значения в последней строке во второй колонке
        scheduleOfficeRizhskaya = List(row.getCell(cellOfficeToRizhskayaType1).getStringCellValue,
          getTimeFromDate(row.getCell(cellOfficeToRizhskayaTime1).getDateCellValue)) :: scheduleOfficeRizhskaya
      }
      // Маппинг Марьина Роща
      if (currRow >= startRowMarinaRoszha) {
        // Маппинг Марьина Роща-Мурманский
        if (currRow <= lastRowMarinaRoszhaOffice) {
          scheduleMarinaRoszhaOffice = List(row.getCell(cellMarinaRoszhaOfficeType).getStringCellValue,
            getTimeFromDate(row.getCell(cellMarinaRoszhaOfficeTime).getDateCellValue)) :: scheduleMarinaRoszhaOffice
        }
        // Маппинг Мурманскйи-Марьина Роща
        if (currRow <= lastRowOfficeMarinaRoszha) scheduleOfficeMarinaRoszha = List(row.getCell(cellOfficeMarinaRoszhaType).getStringCellValue,
          getTimeFromDate(row.getCell(cellOfficeMarinaRoszhaTime).getDateCellValue)) :: scheduleOfficeMarinaRoszha
      }
      currRow += 1
    }
    logger.info("excel file {} load successful", fileName)

    val scheduleBus = Map("ToOffice" -> Map("FromRizhskaya" -> scheduleRizhskayaOffice, "FromMarinaRoszha" -> scheduleMarinaRoszhaOffice),
      "FromOffice" -> Map("ToRizhskaya" -> scheduleOfficeRizhskaya, "ToMarinaRoszha" -> scheduleOfficeMarinaRoszha))
    logger.debug("scheduleBus = {}", scheduleBus)

    scheduleBus
  }


}

trait Schedule {
  val scheduleBus: Map[String, Map[String, List[List[String]]]] = new LoadExcelSchedule(ConfigContextImpl).loadScheduleFromFile()
}

object BusSchedule extends Schedule with LazyLogging {

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
    logger.debug("invoke scheduleFromMarinaRoszha()")
    val scheduleMarinaRoszhaOffice = scheduleBus("ToOffice")("FromMarinaRoszha").filter(list => list.head contains "М").sortBy(list => list(1))
    logger.debug("scheduleMarinaRoszhaOffice = {}", scheduleMarinaRoszhaOffice)
    scheduleMarinaRoszhaOffice
  }

  // Офис - Марьина Роща
  def scheduleOfficeToMarinaRoszha(): List[List[String]] = {
    logger.debug("invoke scheduleOfficeToMarinaRoszha()")
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
    logger.debug("invoke scheduleToOffice({})", date)
    val scheduleFromRizhskaya = scheduleBus("ToOffice")("FromRizhskaya").filter(list => list(1) > date).sortBy(list => list(1))
    val scheduleFromMarinaRoszha = scheduleBus("ToOffice")("FromMarinaRoszha").filter(list => list(1) > date).sortBy(list => list(1))

    List(scheduleFromRizhskaya, scheduleFromMarinaRoszha)
  }
}
