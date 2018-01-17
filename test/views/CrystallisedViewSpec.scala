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

package views

import assets.Messages
import assets.Messages.{Sidebar => sidebarMessages}
import assets.TestConstants.BusinessDetails._
import assets.TestConstants.CalcBreakdown._
import assets.TestConstants.Estimates._
import assets.TestConstants.PropertyIncome._
import assets.TestConstants._
import auth.MtdItUser
import config.FrontendAppConfig
import models.{CalculationDataModel, IncomeSourcesModel}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages.Implicits._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.ImplicitCurrencyFormatter._
import utils.{ImplicitCurrencyFormatter, TestSupport}

class CrystallisedViewSpec extends TestSupport {

  lazy val mockAppConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  val testIncomeSources: IncomeSourcesModel = IncomeSourcesModel(List(businessIncomeModelAlignedTaxYear), Some(propertyIncomeModel))
  val testMtdItUser: MtdItUser[_] = MtdItUser(testMtditid, testNino, Some(testUserDetails), testIncomeSources)(FakeRequest())
  val testBusinessIncomeSource: IncomeSourcesModel = IncomeSourcesModel(List(businessIncomeModelAlignedTaxYear), None)
  val testPropertyIncomeSource: IncomeSourcesModel = IncomeSourcesModel(List.empty, Some(propertyIncomeModel))

  private def pageSetup(calcDataModel: CalculationDataModel, incomeSources: IncomeSourcesModel) = new {
    lazy val page: HtmlFormat.Appendable = views.html.crystallised(
      CalcBreakdown.calculationDisplaySuccessModel(calcDataModel),
      testYear)(serviceInfo)(FakeRequest(),applicationMessages, mockAppConfig, testMtdItUser, incomeSources)
    lazy val document: Document = Jsoup.parse(contentAsString(page))

    implicit val model: CalculationDataModel = calcDataModel
  }

  "The Crystallised view" should {

    val setup = pageSetup(busPropBRTCalcDataModel, testIncomeSources)
    import setup._
    val messages = new Messages.Calculation(testYear)
    val crysMessages = new Messages.Calculation(testYear).Crystallised

    s"have the title '${crysMessages.tabTitle}'" in {
      document.title() shouldBe crysMessages.tabTitle
    }

    s"have the tax year '${crysMessages.subHeading}'" in {
      document.getElementById("tax-year").text() shouldBe crysMessages.subHeading
    }

    s"have the page heading '${crysMessages.heading}'" in {
      document.getElementById("page-heading").text() shouldBe crysMessages.heading
    }

    s"have an Owed Tax section" which {

      lazy val owedTaxSection = document.getElementById("owed-tax")

      "has a section for What you owe" which {

        lazy val wyoSection = owedTaxSection.getElementById("whatYouOwe")

        s"has the correct 'What you owe' heading" in {
          wyoSection.getElementById("whatYouOweHeading").text shouldBe crysMessages.wyoHeading(busPropBRTCalcDataModel.totalIncomeTaxNicYtd.toCurrencyString)
        }

        s"has the correct 'whatYouOwe' p1 paragraph '${crysMessages.p1}'" in {
          wyoSection.getElementById("inYearP1").text shouldBe crysMessages.p1
        }

      }

    }

    "have a Calculation Breakdown" that {

      "for users with both a property and a business" which {
        "have just the basic rate of tax" should {
          val total = (model.incomeReceived.ukProperty + model.incomeReceived.selfEmployment).toCurrencyString
          val setup = pageSetup(busPropBRTCalcDataModel, testIncomeSources)
          import setup._

          s"have a heading of ${crysMessages.breakdownHeading}" in {
            document.getElementById("howCalculatedHeading").text shouldBe crysMessages.breakdownHeading
          }

          s"have a business profit section amount of ${model.incomeReceived.selfEmployment}" in {
            document.getElementById("business-profit-heading").text shouldBe messages.InYearEstimate.CalculationBreakdown.businessProfit
            document.getElementById("business-profit").text shouldBe total
          }

          s"have a personal allowance amount of ${model.personalAllowance}" in {
            document.getElementById("personal-allowance-heading").text shouldBe messages.InYearEstimate.CalculationBreakdown.personalAllowance
            document.getElementById("personal-allowance").text shouldBe "-"+model.personalAllowance.toCurrencyString
          }

          s"have a taxable income amount of ${model.totalTaxableIncome}" in {
            document.getElementById("taxable-income-heading").text shouldBe messages.InYearEstimate.CalculationBreakdown.yourTaxableIncome
            document.getElementById("taxable-income").text shouldBe model.totalTaxableIncome.toCurrencyString
          }

          s"have an income tax section" which {
            "has the correct amount of income taxed at BRT" in {
              document.getElementById("brt-it-calc").text shouldBe model.payPensionsProfit.basicBand.taxableIncome.toCurrencyString
            }
            "has the correct BRT rate" in {
              document.getElementById("brt-rate").text shouldBe model.payPensionsProfit.basicBand.taxRate.toString
            }
            "has the correct tax charged at BRT" in {
              document.getElementById("brt-amount").text shouldBe model.payPensionsProfit.basicBand.taxAmount.toCurrencyString
            }
          }
          s"have a National Insurance Class 2 amount of ${model.nic.class2}" in {
            document.getElementById("nic2-amount-heading").text shouldBe messages.InYearEstimate.CalculationBreakdown.nic2
            document.getElementById("nic2-amount").text shouldBe model.nic.class2.toCurrencyString
          }
          s"have a National Insurance Class 4 amount of ${model.nic.class4}" in {
            document.getElementById("nic4-amount-heading").text shouldBe messages.InYearEstimate.CalculationBreakdown.nic4
            document.getElementById("nic4-amount").text shouldBe model.nic.class4.toCurrencyString
          }
          s"have a total tax estimate of ${model.totalIncomeTaxNicYtd}" in {
            document.getElementById("total-estimate-heading").text shouldBe messages.InYearEstimate.CalculationBreakdown.total
            document.getElementById("total-estimate").text shouldBe model.totalIncomeTaxNicYtd.toCurrencyString
          }
        }

        "have the higher rate of tax" should {
          val setup = pageSetup(busBropHRTCalcDataModel, testIncomeSources)
          import ImplicitCurrencyFormatter._
          import setup._

          s"have an income tax section" which {
            "has a BRT section" in {
              document.getElementById("brt-section") should not be null
            }

            "has the correct amount of income taxed at HRT" in {
              document.getElementById("hrt-it-calc").text shouldBe model.payPensionsProfit.higherBand.taxableIncome.toCurrencyString
            }
            "has the correct HRT rate" in {
              document.getElementById("hrt-rate").text shouldBe model.payPensionsProfit.higherBand.taxRate.toString
            }
            "has the correct tax charged at HRT" in {
              document.getElementById("hrt-amount").text shouldBe model.payPensionsProfit.higherBand.taxAmount.toCurrencyString
            }

            "does not have an ART section" in {
              document.getElementById("art-section") shouldBe null
            }
          }
        }

        "have the additional rate of tax" should {
          val setup = pageSetup(busPropARTCalcDataModel, testIncomeSources)
          import ImplicitCurrencyFormatter._
          import setup._
          s"have an income tax section" which {
            "has a BRT section" in {
              document.getElementById("brt-section") should not be null
            }
            "has a HRT section" in {
              document.getElementById("hrt-section") should not be null
            }

            "has the correct amount of income taxed at ART" in {
              document.getElementById("art-it-calc").text shouldBe model.payPensionsProfit.additionalBand.taxableIncome.toCurrencyString
            }
            "has the correct ART rate" in {
              document.getElementById("art-rate").text shouldBe model.payPensionsProfit.additionalBand.taxRate.toString
            }
            "has the correct tax charged at ART" in {
              document.getElementById("art-amount").text shouldBe model.payPensionsProfit.additionalBand.taxAmount.toCurrencyString
            }
          }
        }

        "has no taxable income and no NI contributions" should {
          val setup = pageSetup(noTaxOrNICalcDataModel, testIncomeSources)
          import ImplicitCurrencyFormatter._
          import setup._
          s"have a taxable income amount of ${model.totalTaxableIncome}" in {
            document.getElementById("taxable-income").text shouldBe model.totalTaxableIncome.toCurrencyString
          }
          s"not have a National Insurance amount" in {
            document.getElementById("ni-amount") shouldBe null
          }
          s"have a total tax estimate of ${model.totalIncomeTaxNicYtd}" in {
            document.getElementById("total-estimate").text shouldBe model.totalIncomeTaxNicYtd.toCurrencyString
          }
        }

        "has no taxable income and some NI contribution" should {
          val setup = pageSetup(noTaxJustNICalcDataModel, testIncomeSources)
          import ImplicitCurrencyFormatter._
          import setup._
          s"have a taxable income amount of ${model.totalTaxableIncome}" in {
            document.getElementById("taxable-income").text shouldBe model.totalTaxableIncome.toCurrencyString
          }
          s"have a National Insurance Class 2 amount of ${model.nic.class2}" in {
            document.getElementById("nic2-amount").text shouldBe model.nic.class2.toCurrencyString
          }
          s"have a National Insurance Class 4 amount of ${model.nic.class4}" in {
            document.getElementById("nic4-amount").text shouldBe model.nic.class4.toCurrencyString
          }
          s"have a total tax estimate of ${model.totalIncomeTaxNicYtd}" in {
            document.getElementById("total-estimate").text shouldBe model.totalIncomeTaxNicYtd.toCurrencyString
          }
        }
      }

      "when no breakdown data is retrieved" should {
        lazy val noBreakdownPage = views.html.estimatedTaxLiability(
          CalcBreakdown.calculationDisplayNoBreakdownModel, testYear)(serviceInfo)(FakeRequest(), applicationMessages, mockAppConfig, testMtdItUser, testIncomeSources)
        lazy val noBreakdownDocument = Jsoup.parse(contentAsString(noBreakdownPage))

        "not display a breakdown section" in {
          noBreakdownDocument.getElementById("calc-breakdown-inner-link") shouldBe null
        }
      }

      "when the user only has businesses registered" should {

        val setup = pageSetup(justBusinessCalcDataModel, testBusinessIncomeSource)
        import setup._

        "display the business profit amount" in {
          document.getElementById("business-profit").text shouldBe "£3,000"
        }

        "not display the property profit section" in {
          document.getElementById("property-profit-section") shouldBe null
        }
      }

      "when the user only has a business registered but has a property profit value" should {
        val setup = pageSetup(busPropBRTCalcDataModel, testBusinessIncomeSource)
        import setup._
        "display the business heading" in {
          document.getElementById("business-profit-heading").text shouldBe "Business profit"
        }
        "display the business profit amount" in {
          document.getElementById("business-profit").text shouldBe "£3,000"
        }
      }

      "when the user only has properties registered" should {

        val setup = pageSetup(justPropertyCalcDataModel, testPropertyIncomeSource)
        import setup._

        "display the property heading" in {
          document.getElementById("business-profit-heading").text shouldBe "Property profit"
        }
        "display the property profit section" in {
          document.getElementById("business-profit").text shouldBe "£3,000"
        }
      }

      "when the user only has properties registered but has a business profit value" should {
        val setup = pageSetup(busPropBRTCalcDataModel, testPropertyIncomeSource)
        import setup._

        "display the business heading" in {
          document.getElementById("business-profit-heading").text shouldBe "Business profit"
        }
        "display the business profit amount" in {
          document.getElementById("business-profit").text shouldBe "£3,000"
        }
      }

    }

    "have sidebar section " in {
      document.getElementById("sidebar") shouldNot be(null)
    }

    "have a couple of sentences about adjustments" in {
      document.getElementById("adjustments").text shouldBe crysMessages.errors
      document.getElementById("changes").text shouldBe crysMessages.changes
    }
    
  }

}