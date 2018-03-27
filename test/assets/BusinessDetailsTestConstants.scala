/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package assets

import assets.ReportDeadlinesTestConstants._
import assets.BaseTestConstants.{testErrorMessage, testErrorStatus, testSelfEmploymentId, testSelfEmploymentId2}
import models.core._
import models.incomeSourceDetails.BusinessDetailsModel
import models.incomeSourcesWithDeadlines.BusinessIncomeModel
import models.reportDeadlines.{ReportDeadlineModel, ReportDeadlinesModel}

object BusinessDetailsTestConstants {

  val testBusinessAccountingPeriod = AccountingPeriodModel(start = "2017-6-1", end = "2018-5-30")
  val test2018BusinessAccountingPeriod = AccountingPeriodModel(start = "2017-3-5", end = "2018-3-6")
  val testTradeName = "business"
  val testTradeName2 = "business"
  val testBizAddress = AddressModel(
    addressLine1 = "64 Zoo Lane",
    addressLine2 = Some("Happy Place"),
    addressLine3 = Some("Magical Land"),
    addressLine4 = Some("England"),
    postCode = Some("ZL1 064"),
    countryCode = "UK"
  )
  val testContactDetails = ContactDetailsModel(Some("123456789"),Some("0123456789"),Some("8008135"),Some("google@chuckNorris.com"))
  val testCessation = CessationModel(Some("2018-1-1".toLocalDate), Some("It was a stupid idea anyway"))

  val businessDetailsSuccess = BusinessDetailsModel(
    incomeSourceId = testSelfEmploymentId,
    accountingPeriod = testBusinessAccountingPeriod,
    tradingName = Some(testTradeName),
    address = Some(testBizAddress),
    contactDetails = Some(testContactDetails),
    tradingStartDate = Some(""),
    cashOrAccruals = Some(""),
    seasonal = Some(true),
    cessation = Some(testCessation),
    paperless = Some(true)
  )

  val business1 = BusinessDetailsModel(
    incomeSourceId = testSelfEmploymentId,
    accountingPeriod = testBusinessAccountingPeriod,
    cashOrAccruals = Some("CASH"),
    tradingStartDate = Some("2017-1-1"),
    cessation = None,
    tradingName = Some(testTradeName),
    address = Some(testBizAddress),
    contactDetails = None,
    seasonal = None,
    paperless = None
  )


  val business2 = BusinessDetailsModel(
    incomeSourceId = testSelfEmploymentId2,
    accountingPeriod = testBusinessAccountingPeriod,
    cashOrAccruals = Some("CASH"),
    tradingStartDate = Some("2017-1-1"),
    cessation = None,
    tradingName = Some(testTradeName2),
    address = Some(testBizAddress),
    contactDetails = None,
    seasonal = None,
    paperless = None
  )

  val ceasedBusiness = BusinessDetailsModel(
    incomeSourceId = testSelfEmploymentId,
    accountingPeriod = testBusinessAccountingPeriod,
    cashOrAccruals = Some("CASH"),
    tradingStartDate = Some("2017-1-1"),
    cessation = Some(CessationModel(
      date = Some("2018-5-30"),
      reason = Some("It was a really, really bad idea")
    )),
    tradingName = Some(testTradeName),
    address = Some(testBizAddress),
    contactDetails = None,
    seasonal = None,
    paperless = None
  )

  val businessErrorModel = ErrorModel(testErrorStatus, testErrorMessage)

  val receivedObligation = ReportDeadlineModel(
    start = "2017-04-01",
    end = "2017-6-30",
    due = "2017-7-31",
    met = true
  )

  val overdueObligation = ReportDeadlineModel(
    start = "2017-7-1",
    end = "2017-9-30",
    due = "2017-10-30",
    met = false
  )

  val openObligation = ReportDeadlineModel(
    start = "2017-7-1",
    end = "2017-9-30",
    due = "2017-10-31",
    met = false
  )

  val obligationsDataSuccessModel: ReportDeadlinesModel = ReportDeadlinesModel(List(receivedObligation, overdueObligation, openObligation))

  val businessIncomeModel =
    BusinessIncomeModel(
      testSelfEmploymentId,
      testTradeName,
      None,
      testBusinessAccountingPeriod,
      obligationsDataSuccessModel
    )

  val businessIncomeModel2 =
    BusinessIncomeModel(
      testSelfEmploymentId2,
      testTradeName2,
      None,
      testBusinessAccountingPeriod,
      obligationsDataSuccessModel
    )

  val business2018IncomeModel =
    BusinessIncomeModel(
      testSelfEmploymentId,
      testTradeName,
      None,
      test2018BusinessAccountingPeriod,
      obligationsDataSuccessModel
    )

  val businessIncomeModelAlignedTaxYear =
    BusinessIncomeModel(
      testSelfEmploymentId,
      testTradeName,
      None,
      AccountingPeriodModel(start = "2017-4-6", end = "2018-4-5"),
      obligationsDataSuccessModel
    )

  val business2019IncomeModel =
    BusinessIncomeModel(
      testSelfEmploymentId,
      testTradeName,
      None,
      AccountingPeriodModel(start = "2018-3-5", end = "2019-3-6"),
      obligationsDataSuccessModel
    )
}