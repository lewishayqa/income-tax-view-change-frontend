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

import assets.BaseTestConstants._
import assets.BusinessDetailsTestConstants._
import assets.IncomeSourceDetailsTestConstants._
import assets.Messages
import assets.Messages.{Breadcrumbs => breadcrumbMessages}
import assets.PropertyDetailsTestConstants._
import auth.MtdItUser
import config.FrontendAppConfig
import models.incomeSourceDetails.{BusinessDetailsModel, PropertyDetailsModel}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages.Implicits.applicationMessages
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestSupport

class AccountDetailsViewSpec extends TestSupport {

  lazy val mockAppConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  val testMtdItUser: MtdItUser[_] = MtdItUser(testMtditid, testNino, Some(testUserDetails), businessAndPropertyAligned)(FakeRequest())

  private def pageSetup(businesses: List[(BusinessDetailsModel,Int)], properties: Option[PropertyDetailsModel]) = new {
    lazy val page: HtmlFormat.Appendable = views.html.accountDetailsView(
      businesses, properties)(FakeRequest(),applicationMessages, mockAppConfig, testMtdItUser)
    lazy val document: Document = Jsoup.parse(contentAsString(page))
  }

  "The Account Details view" when {

    "passed a business and a property" should {

      val setup = pageSetup(List((business1, 0)), Some(propertyDetails))
      import setup._
      val messages = Messages.AccountDetails

      s"have the title '${messages.title}'" in {
        document.title() shouldBe messages.title
      }

      "have a breadcrumb trail" in {
        document.getElementById("breadcrumb-bta").text shouldBe breadcrumbMessages.bta
        document.getElementById("breadcrumb-it").text shouldBe breadcrumbMessages.it
        document.getElementById("breadcrumb-account").text shouldBe breadcrumbMessages.details
      }

      s"have the page heading '${messages.heading}'" in {
        document.getElementById("page-heading").text() shouldBe messages.heading
      }

      s"have a 'your-businesses' section" in {
        document.getElementById("your-businesses").text() shouldBe messages.yourBusinesses
        document.getElementById("business-link-1").text() shouldBe "business"
      }

      s"have a 'your-properties' section" in {
        document.getElementById("your-properties").text() shouldBe messages.yourProperties
        document.getElementById("reporting-period").text() shouldBe messages.reportingPeriod("6 April", "5 April")
      }

      "show a back link to the Income Tax home page" in {
        Option(document.getElementById("it-home-back")) shouldNot be(None)
      }

    }

    "only passed a business" should {

      val setup = pageSetup(List((business1, 0)), None)
      import setup._
      val messages = Messages.AccountDetails

      s"have the title '${messages.title}'" in {
        document.title() shouldBe messages.title
      }

      "have a breadcrumb trail" in {
        document.getElementById("breadcrumb-bta").text shouldBe breadcrumbMessages.bta
        document.getElementById("breadcrumb-it").text shouldBe breadcrumbMessages.it
        document.getElementById("breadcrumb-account").text shouldBe breadcrumbMessages.details
      }

      s"have the page heading '${messages.heading}'" in {
        document.getElementById("page-heading").text() shouldBe messages.heading
      }

      s"have a 'your-businesses' section" in {
        document.getElementById("your-businesses").text() shouldBe messages.yourBusinesses
        document.getElementById("business-link-1").text() shouldBe "business"
      }

      s"not have a 'your-properties' section" in {
        Option(document.getElementById("your-properties")) shouldBe None
        Option(document.getElementById("reporting-period")) shouldBe None
      }

      "show a back link to the Income Tax home page" in {
        Option(document.getElementById("it-home-back")) shouldNot be(None)
      }
    }

    "only passed a property" should {

      val setup = pageSetup(List(), Some(propertyDetails))
      import setup._
      val messages = Messages.AccountDetails

      s"have the title '${messages.title}'" in {
        document.title() shouldBe messages.title
      }

      "have a breadcrumb trail" in {
        document.getElementById("breadcrumb-bta").text shouldBe breadcrumbMessages.bta
        document.getElementById("breadcrumb-it").text shouldBe breadcrumbMessages.it
        document.getElementById("breadcrumb-account").text shouldBe breadcrumbMessages.details
      }

      s"have the page heading '${messages.heading}'" in {
        document.getElementById("page-heading").text() shouldBe messages.heading
      }

      s"not have a 'your-businesses' section" in {
        Option(document.getElementById("your-businesses")) shouldBe None
        Option(document.getElementById("business-link-1")) shouldBe None
      }

      s"have a 'your-properties' section" in {
        document.getElementById("your-properties").text() shouldBe messages.yourProperties
        document.getElementById("reporting-period").text() shouldBe messages.reportingPeriod("6 April", "5 April")
      }

      "not have a ceased-properties section" in {
        Option(document.getElementById("ceased-properties")) shouldBe None
      }

      "show a back link to the Income Tax home page" in {
        Option(document.getElementById("it-home-back")) shouldNot be(None)
      }

    }

    "only passed a ceased property" should {

      val setup = pageSetup(List(), Some(ceasedPropertyDetails))
      import setup._
      val messages = Messages.AccountDetails

      s"have the title '${messages.title}'" in {
        document.title() shouldBe messages.title
      }

      "have a breadcrumb trail" in {
        document.getElementById("breadcrumb-bta").text shouldBe breadcrumbMessages.bta
        document.getElementById("breadcrumb-it").text shouldBe breadcrumbMessages.it
        document.getElementById("breadcrumb-account").text shouldBe breadcrumbMessages.details
      }

      s"have the page heading '${messages.heading}'" in {
        document.getElementById("page-heading").text() shouldBe messages.heading
      }

      s"not have a 'your-businesses' section" in {
        Option(document.getElementById("your-businesses")) shouldBe None
        Option(document.getElementById("business-link-1")) shouldBe None
      }

      s"have a 'your-properties' section" in {
        document.getElementById("your-properties").text() shouldBe messages.yourProperties
        document.getElementById("reporting-period").text() shouldBe messages.reportingPeriod("6 April", "5 April")
      }

      "have a ceased-properties section" in {
        document.getElementById("ceased-properties").text() shouldBe messages.ceasedProperties("1 January 2018")
      }

      "show a back link to the Income Tax home page" in {
        Option(document.getElementById("it-home-back")) shouldNot be(None)
      }

    }

  }

}
