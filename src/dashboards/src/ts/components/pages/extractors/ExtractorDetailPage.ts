import m from 'mithril';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { GrabberConfigDetail } from '../../../auto/pieroxy-conkw';
import { Endpoints } from '../../../utils/navigation/Endpoints';
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
      m(".content", m("", this.configuration.detailsMetadata.className)
      )
    ]);
  }
}

export interface ExtractorDetailPageAttrs {
  className:string;
  name:string;
}