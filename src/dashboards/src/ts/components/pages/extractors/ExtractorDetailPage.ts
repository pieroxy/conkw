import m from 'mithril';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { GrabberConfigDetail } from '../../../auto/pieroxy-conkw';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { SelectInput } from '../../atoms/forms/SelectInput';
import { HomeIcon } from '../../atoms/icons/HomeIcon';
import { RightChevronIcon } from '../../atoms/icons/RightChevronIcon';
import { Link } from '../../atoms/Link';
import { AbstractPage } from '../AbstractPage';

export class ExtractorDetailPage extends AbstractPage<ExtractorDetailPageAttrs> {
  configuration:GrabberConfigDetail;

  getPageTitle(): string {
    return "Edit grabber";
  }

  oninit({attrs}:m.Vnode<ExtractorDetailPageAttrs>) {
    ApiEndpoints.GetGrabberDetail.call({
      implementation:attrs.className,
      name:attrs.name
    }).then(response => this.configuration = response.config);
  }

  render():m.Children {
    return m(".page", [
      m(".title", [
        m(Link, {tooltip:"Go back Home", target:Endpoints.HOME}, m(HomeIcon)),
        m(RightChevronIcon),
        "Grabbers", 
      ]),
      !this.configuration ? "" : m(".content", 
        m("", "Configuration for " + this.configuration.implementation),
        m("table", [
          m("tr", [
            m("td", "Log level"),
            m("td", m(SelectInput, {
              refHolder:this.configuration,
              refProperty:"logLevel",
              onenter:()=>{},
              values:[
                {id:"OFF", label:"OFF"},
                {id:"SEVERE", label:"SEVERE"},
                {id:"WARNING", label:"WARNING"},
                {id:"INFO", label:"INFO"},
                {id:"CONFIG", label:"CONFIG"},
                {id:"FINE", label:"FINE"},
                {id:"FINER", label:"FINER"},
                {id:"FINEST", label:"FINEST"},
                {id:"ALL", label:"ALL"},
              ]
            }))
          ])
        ])
      )
    ]);
  }
}

export interface ExtractorDetailPageAttrs {
  className:string;
  name:string;
}