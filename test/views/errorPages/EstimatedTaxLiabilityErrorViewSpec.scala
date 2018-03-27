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

package views.errorPages

import assets.Messages.{EstimatedTaxLiabilityError => messages}
import assets.BusinessDetailsTestConstants._
import assets.EstimatesTestConstants._
import assets.PropertyDetailsTestConstants._
import assets.BaseTestConstants._
import auth.MtdItUser
import config.FrontendAppConfig
import models.incomeSourcesWithDeadlines.IncomeSourcesModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages.Implicits._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestSupport

class EstimatedTaxLiabilityErrorViewSpec extends TestSupport {

  lazy val mockAppConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  val testIncomeSources: IncomeSourcesModel = IncomeSourcesModel(List(businessIncomeModel), Some(propertyIncomeModel))
  val testMtdItUser: MtdItUser[_] = MtdItUser(testMtditid, testNino, Some(testUserDetails), testIncomeSources)(FakeRequest())

  "The EstimatedTaxLiabilityError view" should {

    lazy val page: HtmlFormat.Appendable =
      views.html.errorPages.estimatedTaxLiabilityError(testYear)(FakeRequest(), applicationMessages, mockAppConfig, testMtdItUser, testIncomeSources)
    lazy val document: Document = Jsoup.parse(contentAsString(page))

    s"have the title '${messages.title}'" in {
      document.title() shouldBe messages.title
    }

    s"have the tax year '${messages.taxYearSubheading}'" in {
      document.getElementById("tax-year").text() shouldBe messages.taxYearSubheading
    }

    s"have the page heading '${messages.pageHeading}'" in {
      document.getElementById("page-heading").text() shouldBe messages.pageHeading
    }

    s"have an Estimated Tax Liability section" which {

      lazy val estimateSection = document.getElementById("estimated-tax")

      s"has a paragraph with '${messages.p1}'" in {
        estimateSection.getElementById("p1").text() shouldBe messages.p1
      }

      s"has a paragraph with '${messages.p2}'" in {
        estimateSection.getElementById("p2").text() shouldBe messages.p2
      }
    }

    "show a back link to the Income Tax home page" in {
      lazy val page: HtmlFormat.Appendable =
        views.html.errorPages.estimatedTaxLiabilityError(testYear)(FakeRequest(), applicationMessages, mockAppConfig, testMtdItUser, testIncomeSources)
      lazy val document: Document = Jsoup.parse(contentAsString(page))
      document.getElementById("it-home-back") shouldNot be(null)
    }
  }
}