import m from 'mithril';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { ConfigurationObjectFieldMetadata, ConfigurationObjectFieldType, GetGrabberDetailOutput } from '../../../auto/pieroxy-conkw';
import { DisplayUtils } from '../../../utils/DisplayUtils';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Notification, Notifications, NotificationsClass, NotificationsType } from '../../../utils/Notifications';
import { Button } from '../../atoms/forms/Button';
import { MultipleSelectInput } from '../../atoms/forms/MultipleSelectInput';
import { SelectInput } from '../../atoms/forms/SelectInput';
import { TextFieldInput } from '../../atoms/forms/TextFieldInput';
import { HomeIcon } from '../../atoms/icons/HomeIcon';
import { RightChevronIcon } from '../../atoms/icons/RightChevronIcon';
import { Link } from '../../atoms/Link';
import { AbstractPage } from '../AbstractPage';

export class ExtractorDetailPage extends AbstractPage<ExtractorDetailPageAttrs> {
  configuration:GetGrabberDetailOutput;

  getPageTitle(): string {
    return "Edit grabber";
  }

  oninit({attrs}:m.Vnode<ExtractorDetailPageAttrs>) {
    ApiEndpoints.GetGrabberDetail.call({
      implementation:attrs.className,
      name:attrs.name.substring(2)
    }).then(response => this.configuration = response);
  }

  render({attrs}:m.Vnode<ExtractorDetailPageAttrs>):m.Children {
    return m(".page", [
      m(".title", [
        m("span.floatright", m(Button, { action:() => {
          ApiEndpoints.TestGrabberConfiguration.call({
            configuration:this.configuration.config,
            grabberImplementation:attrs.className
          })
        }}, "Save")),
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
            m("td.monospace", m.trust(this.configuration.config.implementation.replace(/\./g, ".<span style='width:0px;overflow:hidden;display:inline-block;'> </span>"))),
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
          this.configuration.detailsMetadata.fields.map(md => this.generateField(this.configuration.config.config, md))
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
              refProperty:field.name,
              spellcheck:false
            }), !field.defaultValue ? null:m(".defaultValue", [
              "Default: ",
              m("span.monospace", field.defaultValue)
            ]))
          ]);
      }
    } else { // field.list is true
      return this.generateList(holder, field);
    }
    return m("tr", [
      m("td", field.label),
      m("td", "Not implemented yet")
    ])
  }
  
  generateList(holder: any, field: ConfigurationObjectFieldMetadata): m.Children {
    let count=1;
    if (field.possibleValues && field.possibleValues.length) {
      return m("tr", [
        m("td", {colspan:2}, [
          m("", [
            field.label
          ]),
          m(".hsplit50", [
            m(MultipleSelectInput, {
              refHolder:holder,
              refProperty:field.name,
              onenter:()=>{},
              values:field.possibleValues
            }),
            m("", [
              "Values: ",
              (holder[field.name] && holder[field.name].length) ? (<string[]>holder[field.name]).map(v => m("span.monospace.rm10.lm10", v+" ")) : "None selected"
            ])
          ])
        ]),

      ])       
    } else {
      return m("tr", [
        m("td", {colspan:2}, [
          m("", [
            field.label,
            m(".spacer"),
            m(Button, {
              action:()=>{
                let array = holder[field.name];
                if (!array) {
                  array = holder[field.name] = [];
                }
                switch (field.type) {
                  case ConfigurationObjectFieldType.STRING:
                    array.push("");
                    break;
                  default:
                    Notifications.addNotification(new Notification(NotificationsClass.LOGIN, NotificationsType.ERROR, "Not implemented yet!", 5))
                    return;
                }
              },
              secondary:true
            }, "Add")
          ]),
          m("table", 
            ((<[]>holder[field.name])||[]).map(e => {
              let ifield = <ConfigurationObjectFieldMetadata>JSON.parse(JSON.stringify(field));
              ifield.list = false;
              let itemLabel = field.listItemsName || "Item"
              ifield.label = itemLabel + " #" + count++;
              return [
                this.generateField(e, ifield)
              ];
            })
          )
        ]),
      ]);
    }
  }
}

export interface ExtractorDetailPageAttrs {
  className:string;
  name:string;
}
