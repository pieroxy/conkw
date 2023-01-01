import m from 'mithril';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { ConfigurationObjectFieldMetadata, ConfigurationObjectFieldType, GetGrabberDetailOutput, GrabberConfigMessage, SaveGrabberConfigurationOutput } from '../../../auto/pieroxy-conkw';
import { DisplayUtils } from '../../../utils/DisplayUtils';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Routing } from '../../../utils/navigation/Routing';
import { Notification, Notifications, NotificationsClass, NotificationsType } from '../../../utils/Notifications';
import { Status, StatusMessageInterface } from '../../../utils/types';
import { Button } from '../../atoms/forms/Button';
import { MultipleSelectInput } from '../../atoms/forms/MultipleSelectInput';
import { SelectInput } from '../../atoms/forms/SelectInput';
import { TextFieldInput } from '../../atoms/forms/TextFieldInput';
import { HomeIcon } from '../../atoms/icons/HomeIcon';
import { RightChevronIcon } from '../../atoms/icons/RightChevronIcon';
import { StatusErrorIcon } from '../../atoms/icons/StatusErrorIcon';
import { StatusOkIcon } from '../../atoms/icons/StatusOkIcon';
import { StatusWarningIcon } from '../../atoms/icons/StatusWarningIcon';
import { Link } from '../../atoms/Link';
import { AbstractPage } from '../AbstractPage';

export class ExtractorDetailPage extends AbstractPage<ExtractorDetailPageAttrs> {
  configuration:GetGrabberDetailOutput;
  messages:GrabberConfigMessage[];

  getPageTitle(): string {
    return "Edit grabber";
  }

  oninit({attrs}:m.Vnode<ExtractorDetailPageAttrs>) {
    ApiEndpoints.GetGrabberDetail.call({
      implementation:attrs.className,
      name:attrs.name
    }).then(response => this.configuration = response);
  }

  render({attrs}:m.Vnode<ExtractorDetailPageAttrs>):m.Children {
    return m(".page", [
      m(".title", [
        m("span.floatright", m(Button, { action:() => {
          ApiEndpoints.SaveGrabberConfiguration.call({
            configuration:this.configuration.config,
            grabberName:attrs.name,
            grabberImplementation:attrs.className,
            forceSave:false
          }).then(output => this.handleSaveResult(attrs.className, attrs.name, output));
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
              refHolder:this.configuration.config,
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
              ],
              status:this.getStatus("", "logLevel")
            }))
          ]),
          m("tr", [
            m("td", "Name"),
            m("td", 
              m(TextFieldInput, {
                onenter:()=>{},
                refHolder:this.configuration.config,
                refProperty:"name",
                spellcheck:true,
                status:this.getStatus("", "name")
              }),
              m(".defaultValue", [
                "Default: ",
                m("span.monospace", this.configuration.defaultName)
              ])
            )
          ]),
          this.configuration.detailsMetadata.fields.map(md => this.generateField("", this.configuration.config.config, md))
        ])
      )
    ]);
  }
  handleSaveResult(implementation:string, name:string, output: SaveGrabberConfigurationOutput): any {
    if (output.saved) {
      Routing.goToScreen(Endpoints.EXTRACTORS_LIST);
    } else {
      this.messages = output.messages;
      let errs = this.messages.filter(m => m.error).length;
      let warns = this.messages.filter(m => !m.error).length;
      if (errs) {
        Notifications.addNotification(new Notification(
          NotificationsClass.SERVER_RESPONSE,
          NotificationsType.ERROR,
          "The grabber returned errors. Please fix those and try again.",
          3
        ));
        return;
      }
      if (warns) {
        Notifications.addNotification(new Notification(
          NotificationsClass.SERVER_RESPONSE,
          NotificationsType.WARNING,
          [
            "The grabber returned warnings. If you're ok with them, click here: ",
            m(Button, {
              action:() => {
                ApiEndpoints.SaveGrabberConfiguration.call({
                  configuration:this.configuration.config,
                  grabberImplementation:implementation,
                  grabberName:name,
                  forceSave:false
                }).then(output => this.handleSaveResult(implementation,name,output))
      
              }
            }, "Save anyways")
          ],
          12
        ))
      }
    }
  }
  getWarningIcon(prefix:string, name: string): m.Children {
    if (!this.messages) return null;
    let msg = this.messages.filter(m => m.fieldName === (prefix+name));
    if (msg && msg.length) {
      return m("span", {title:msg[0].message}, msg[0].error ? m(StatusErrorIcon) : m(StatusWarningIcon));
    }
    return m(StatusOkIcon);
  }
  getStatus(prefix:string, name: string): undefined|StatusMessageInterface {
    if (!this.messages) return undefined;
    let msg = this.messages.filter(m => m.fieldName === (prefix+name));
    if (msg && msg.length) {
      return {message:msg[0].message, status: msg[0].error ? Status.ERROR : Status.WARNING};
    }
    return {status:Status.OK, message:""};
  }

  generateField(namePrefix:string, holder:any, field:ConfigurationObjectFieldMetadata):m.Children {
    if (!field.list) {
      switch (field.type) {
        case ConfigurationObjectFieldType.STRING:
          return m("tr", [
            m("td", field.label),
            m("td", m(TextFieldInput, {
              onenter:()=>{},
              refHolder:holder,
              refProperty:field.name,
              spellcheck:false,
              status:this.getStatus(namePrefix, field.name)
            }), 
            !field.defaultValue ? null:m(".defaultValue", [
              "Default: ",
              m("span.monospace", field.defaultValue)
            ]))
          ]);
      }
    } else { // field.list is true
      return this.generateList(namePrefix, holder, field);
    }
    return m("tr", [
      m("td", field.label),
      m("td", "Not implemented yet")
    ])
  }
  
  generateList(namePrefix:string, holder: any, field: ConfigurationObjectFieldMetadata): m.Children {
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
              values:field.possibleValues,
              status:this.getStatus("", "logLevel")
            }),
            m("", [
              "Values: ",
              (holder[field.name] && holder[field.name].length) ? (<string[]>holder[field.name]).map(v => m("span.monospace.rm10.lm10", v+" ")) : "None selected"
            ])
          ])
        ]),

      ])       
    } else {
      let array = (<[]>holder[field.name])||[];
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
            }, "Add"),
            array.length ? null : this.getWarningIcon(namePrefix, field.name)
          ]),
          m("table", 
            array.map((_e, idx) => {
              let ifield = <ConfigurationObjectFieldMetadata>JSON.parse(JSON.stringify(field));
              ifield.list = false;
              let itemLabel = field.listItemsName || "Item"
              ifield.label = itemLabel + " #" + count++;
              ifield.name = ""+idx;
              return [
                this.generateField(namePrefix + field.name + ".", array, ifield),
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

