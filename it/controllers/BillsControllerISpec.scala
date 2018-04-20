/*
 * Copyright 2017 HM Revenue & Customs
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
package controllers

import assets.BaseIntegrationTestConstants._
import assets.IncomeSourceIntegrationTestConstants._
import assets.CalcDataIntegrationTestConstants._
import assets.ReportDeadlinesIntegrationTestConstants.multipleReportDeadlinesDataSuccessModel
import config.FrontendAppConfig
import enums.{Crystallised, Estimate}
import helpers.servicemocks._
import helpers.{ComponentSpecBase, GenericStubMethods}
import models.calculation.LastTaxCalculation
import play.api.http.Status._

class BillsControllerISpec extends ComponentSpecBase with GenericStubMethods {

  lazy val appConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  "Calling the BillsController" when {

    "the Bill Feature is enabled" when {

      "isAuthorisedUser with an active enrolment, and a single, valid crystallised estimate" should {

        "return the correct page with bills links" in {

          And("I wiremock stub a successful Income Source Details response with 1 Business and Property income")
          IncomeTaxViewChangeStub.stubGetIncomeSourceDetailsResponse(testMtditid)(OK, businessAndPropertyResponse)

          And("I wiremock stub multiple open and received obligations response")
          IncomeTaxViewChangeStub.stubGetReportDeadlines(testSelfEmploymentId, multipleReportDeadlinesDataSuccessModel)
          IncomeTaxViewChangeStub.stubGetReportDeadlines(testPropertyIncomeId, multipleReportDeadlinesDataSuccessModel)

          And("I wiremock stub a successful Get Last Estimated Tax Liability response")
          val lastTaxCalcResponse =
            LastTaxCalculation(testCalcId,
              "2017-07-06T12:34:56.789Z",
              calculationDataSuccessWithEoYModel.totalIncomeTaxNicYtd,
              Crystallised
            )
          IncomeTaxViewChangeStub.stubGetLastTaxCalc(testNino, testYear, lastTaxCalcResponse)

          When(s"I call GET /report-quarterly/income-and-expenses/view/bills")
          val res = IncomeTaxViewChangeFrontend.getBills

          verifyIncomeSourceDetailsCall(testMtditid)
          verifyReportDeadlinesCall(testSelfEmploymentId, testPropertyIncomeId)
          verifyLastTaxCalculationCall(testNino, testYear)

          Then("The view should have the correct headings and a single tax bill link")
          res should have(
            httpStatus(OK),
            pageTitle("Bills"),
            elementTextByID("finalised-bills")("View your finalised bills:"),
            elementTextByID(s"bills-link-$testYear")(s"2017 to $testYear tax year"),
            nElementsWithClass("bills-link")(1),
            elementTextByID("earlier-bills")("For earlier bills, view your Self Assessment calculations (opens in a new tab).")
          )
        }
      }

      "isAuthorisedUser with an active enrolment, and multiple valid crystallised estimates" should {

        "return the correct page with tax links" in {

          And("I wiremock stub a successful Income Source Details response with Multiple Business and Property income")
          IncomeTaxViewChangeStub.stubGetIncomeSourceDetailsResponse(testMtditid)(OK, multipleBusinessesAndPropertyResponse)

          And("I wiremock stub multiple open and received obligations response")
          IncomeTaxViewChangeStub.stubGetReportDeadlines(testSelfEmploymentId, multipleReportDeadlinesDataSuccessModel)
          IncomeTaxViewChangeStub.stubGetReportDeadlines(otherTestSelfEmploymentId, multipleReportDeadlinesDataSuccessModel)
          IncomeTaxViewChangeStub.stubGetReportDeadlines(testPropertyIncomeId, multipleReportDeadlinesDataSuccessModel)

          And("I wiremock stub a successful Get Last Estimated Tax Liability response")
          val lastTaxCalcResponse =
            LastTaxCalculation(testCalcId,
              "2017-07-06T12:34:56.789Z",
              calculationDataSuccessWithEoYModel.totalIncomeTaxNicYtd,
              Crystallised
            )
          val lastTaxCalcResponse2 =
            LastTaxCalculation(testCalcId2,
              "2017-07-06T12:34:56.789Z",
              calculationDataSuccessWithEoYModel.totalIncomeTaxNicYtd,
              Crystallised
            )
          IncomeTaxViewChangeStub.stubGetLastTaxCalc(testNino, testYear, lastTaxCalcResponse)
          IncomeTaxViewChangeStub.stubGetLastTaxCalc(testNino, testYearPlusOne, lastTaxCalcResponse2)

          When(s"I call GET /report-quarterly/income-and-expenses/view/bills")
          val res = IncomeTaxViewChangeFrontend.getBills

          verifyIncomeSourceDetailsCall(testMtditid)
          verifyReportDeadlinesCall(testSelfEmploymentId, otherTestSelfEmploymentId, testPropertyIncomeId)
          verifyLastTaxCalculationCall(testNino, testYear)
          verifyLastTaxCalculationCall(testNino, testYearPlusOne)

          Then("The view should have the correct headings and two tax bill links")
          res should have(
            httpStatus(OK),
            pageTitle("Bills"),
            elementTextByID("finalised-bills")("View your finalised bills:"),
            elementTextByID(s"bills-link-$testYearPlusOne")(s"$testYear to $testYearPlusOne tax year"),
            elementTextByID(s"bills-link-$testYear")(s"2017 to $testYear tax year"),
            nElementsWithClass("bills-link")(2),
            elementTextByID("earlier-bills")("For earlier bills, view your Self Assessment calculations (opens in a new tab).")
          )
        }
      }

      "isAuthorisedUser with an active enrolment, with a crystallised calculation and a tax estimate" should {
        "return the correct page with just the tax bill link" in {

          And("I wiremock stub a successful Income Source Details response with Multiple Business and Property income")
          IncomeTaxViewChangeStub.stubGetIncomeSourceDetailsResponse(testMtditid)(OK, multipleBusinessesAndPropertyResponse)

          And("I wiremock stub multiple open and received obligations response")
          IncomeTaxViewChangeStub.stubGetReportDeadlines(testSelfEmploymentId, multipleReportDeadlinesDataSuccessModel)
          IncomeTaxViewChangeStub.stubGetReportDeadlines(otherTestSelfEmploymentId, multipleReportDeadlinesDataSuccessModel)
          IncomeTaxViewChangeStub.stubGetReportDeadlines(testPropertyIncomeId, multipleReportDeadlinesDataSuccessModel)

          And("I wiremock stub a successful Get Last Estimated Tax Liability response")
          val lastTaxCalcResponse =
            LastTaxCalculation(
              testCalcId,
              "2017-07-06T12:34:56.789Z",
              calculationDataSuccessWithEoYModel.totalIncomeTaxNicYtd,
              Crystallised
            )
          val crystallisedLastTaxCalcResponse =
            LastTaxCalculation(
              testCalcId2,
              "2017-07-06T12:34:56.789Z",
              calculationDataSuccessModel.totalIncomeTaxNicYtd,
              Estimate
            )
          IncomeTaxViewChangeStub.stubGetLastTaxCalc(testNino, testYear, lastTaxCalcResponse)
          IncomeTaxViewChangeStub.stubGetLastTaxCalc(testNino, testYearPlusOne, crystallisedLastTaxCalcResponse)

          When(s"I call GET /report-quarterly/income-and-expenses/view/bills")
          val res = IncomeTaxViewChangeFrontend.getBills

          verifyIncomeSourceDetailsCall(testMtditid)
          verifyReportDeadlinesCall(testSelfEmploymentId, otherTestSelfEmploymentId, testPropertyIncomeId)
          verifyLastTaxCalculationCall(testNino, testYear)
          verifyLastTaxCalculationCall(testNino, testYearPlusOne)

          Then("The view should have the correct headings and a single tax bill link")
          res should have(
            httpStatus(OK),
            pageTitle("Bills"),
            elementTextByID("finalised-bills")("View your finalised bills:"),
            elementTextByID(s"bills-link-$testYear")(s"2017 to $testYear tax year"),
            nElementsWithClass("bills-link")(1),
            elementTextByID("earlier-bills")("For earlier bills, view your Self Assessment calculations (opens in a new tab).")
          )
        }
      }

      "isAuthorisedUser with an active enrolment, and no tax bills" should {
        "return the correct page with no bills found message" in {

          And("I wiremock stub a successful Income Source Details response with single Business and Property income")
          IncomeTaxViewChangeStub.stubGetIncomeSourceDetailsResponse(testMtditid)(OK, businessAndPropertyResponse)

          And("I wiremock stub multiple open and received obligations response")
          IncomeTaxViewChangeStub.stubGetReportDeadlines(testSelfEmploymentId, multipleReportDeadlinesDataSuccessModel)
          IncomeTaxViewChangeStub.stubGetReportDeadlines(testPropertyIncomeId, multipleReportDeadlinesDataSuccessModel)

          And("I wiremock stub a successful Get Last Estimated Tax Liability response")
          val lastTaxCalcResponse =
            LastTaxCalculation(
              testCalcId,
              "2017-07-06T12:34:56.789Z",
              calculationDataSuccessWithEoYModel.totalIncomeTaxNicYtd,
              Estimate
            )
          IncomeTaxViewChangeStub.stubGetLastTaxCalc(testNino, testYear, lastTaxCalcResponse)

          When(s"I call GET /report-quarterly/income-and-expenses/view/bills")
          val res = IncomeTaxViewChangeFrontend.getBills

          verifyIncomeSourceDetailsCall(testMtditid)
          verifyReportDeadlinesCall(testSelfEmploymentId, testPropertyIncomeId)
          verifyLastTaxCalculationCall(testNino, testYear)

          Then("The view should have the correct headings and a single tax estimate link")
          res should have(
            httpStatus(OK),
            pageTitle("Bills"),
            elementTextByID("no-bills")("You've had no bills since you started reporting through software."),
            nElementsWithClass("bills-link")(0)
          )
        }
      }

      unauthorisedTest("/bills")
    }

    "the Bills Feature is disabled" should {

      "redirect to home page" in {

        appConfig.features.billsEnabled(false)

        When(s"I call GET /report-quarterly/income-and-expenses/view/bills")
        val res = IncomeTaxViewChangeFrontend.getBills

        Then("I should be redirected to the Home Page")
        res should have(
          httpStatus(SEE_OTHER),
          redirectURI(controllers.routes.HomeController.home().url)
        )
      }

      unauthorisedTest("/bills")
    }
  }
}
