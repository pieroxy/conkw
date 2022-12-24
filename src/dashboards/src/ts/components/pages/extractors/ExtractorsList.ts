import m from 'mithril';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { GrabberConfigSummary } from '../../../auto/pieroxy-conkw';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { HomeIcon } from '../../atoms/icons/HomeIcon';
import { NewDocumentIcon } from '../../atoms/icons/NewDocumentIcon';
import { RightChevronIcon } from '../../atoms/icons/RightChevronIcon';
import { Link } from '../../atoms/Link';
import { AbstractPage } from '../AbstractPage';

export class ExtractorsListPage extends AbstractPage<any> {
  configurations:GrabberConfigSummary[] = [];

  getPageTitle(): string {
    return "Not yet";
  }

  oninit() {
    ApiEndpoints.GetGrabbersSummary.call({}).then(response => this.configurations = response.configs);
  }

  getSimpleImplementation(implementation:string):m.Children {
    let index = implementation.lastIndexOf(".");
    if (index < 1) return implementation;
    return m("span", {title:implementation}, implementation.substring(index+1));
  }

  render():m.Children {
    return m(".page", [
      m(".title", [
        m(Link, {className:"floatright", tooltip:"New grabber", target:""}, m(NewDocumentIcon)),
        m(Link, {tooltip:"Go back Home", target:Endpoints.HOME}, m(HomeIcon)),
        m(RightChevronIcon),
        "Grabbers", 
      ]),
      m(".content", m(".list", this.configurations.map((config) => 
        m(".listitem", [
          m("span", this.getSimpleImplementation(config.implementation)),
          m("span.rm10.lm10", config.name ? m("span.highlighted", config.name) : m("span.lowlighted", "default name")),
          config.logLevel ? " - " + config.logLevel : null,
        ])
      )))
    ]);
  }
}

