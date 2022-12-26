import m from 'mithril';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { ConfigurationObjectFieldMetadata, ConfigurationObjectFieldType, GrabberConfigDetail } from '../../../auto/pieroxy-conkw';
import { DisplayUtils } from '../../../utils/DisplayUtils';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { SelectInput } from '../../atoms/forms/SelectInput';
import { TextFieldInput } from '../../atoms/forms/TextFieldInput';
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
      name:attrs.name.substring(2)
    }).then(response => this.configuration = response.config);
  }

  render({attrs}:m.Vnode<ExtractorDetailPageAttrs>):m.Children {
    return m(".page", [
      m(".title", [
        m(Link, {tooltip:"Go back Home", target:Endpoints.HOME}, m(HomeIcon)),
        m(RightChevronIcon),
        m(Link, {target:Endpoints.EXTRACTORS_LIST}, "Grabbers"),
        m(RightChevronIcon),
        DisplayUtils.getSimpleClassNameWithTooltip(attrs.className)
      ]),
      !this.configuration ? "" : m(".content", 
        m("table.grabberconfig", [
          m("tr", [
            m("td", "Configuration for class"),
            m("td.monospace", m.trust(this.configuration.implementation.replace(/\./g, ".<span style='width:0px;overflow:hidden;display:inline-block;'> </span>"))),
          ]),
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
          ]),
          m("tr", [
            m("td", "Default name"),
            m("td", this.configuration.defaultName)
          ]),
          m("tr", [
            m("td", "Name"),
            m("td", m(TextFieldInput, {
              onenter:()=>{},
              refHolder:this.configuration,
              refProperty:"name",
              spellcheck:true
            }))
          ]),
          this.configuration.detailsMetadata.fields.map(md => this.generateField(this.configuration.details, md))
        ])
      )
    ]);
  }

  generateField(holder:any, field:ConfigurationObjectFieldMetadata):m.Children {
    if (!field.list) {
      switch (field.type) {
        case ConfigurationObjectFieldType.STRING:
          return m("tr", [
            m("td", field.label),
            m("td", m(TextFieldInput, {
              onenter:()=>{},
              refHolder:holder,
              refProperty:"name",
              spellcheck:false
            }), !field.defaultValue ? null:m(".defaultValue", [
              "Default: ",
              m("span.monospace", field.defaultValue)
            ]))
          ]);
      }
    }
    return m("tr", [
      m("td", field.label),
      m("td", "Not implemented yet")
    ])
}
}

export interface ExtractorDetailPageAttrs {
  className:string;
  name:string;
}