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

package views.helpers

import models.{Open, Overdue, Received}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.twirl.api.Html
import utils.ImplicitDateFormatter._
import utils.TestSupport

class TaxYearRenderHelperSpec extends TestSupport {

  "The TaxYearRenderHelper.renderTaxYear method" should {
    "Render the 2018 Tax Year as 2017/18" in {
      TaxYearRenderHelper.renderTaxYear(2018) shouldBe "2017/18"
    }

    "Render the 2018 Payment Date as 31 January 2019" in {
      TaxYearRenderHelper.renderPaymentDueDate(2018) shouldBe "31 January 2019"
    }
  }
}