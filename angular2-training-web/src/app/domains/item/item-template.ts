import {BaseDomainTemplate} from "../../common/templates/baseDomain.template";
import {Item} from "./Item";
import {Injectable} from "@angular/core";
import {DomainMetadata} from "../../common/templates/domain-metadata";

@Injectable()
export class ItemTemplate extends BaseDomainTemplate<Item> {

  constructor() {
    super();
  }

  protected initMetadataArray(): DomainMetadata[] {
    return [
      new DomainMetadata({key:'id', controlType:'textbox', table:false, label:'Id', required:false}),
      new DomainMetadata({key:'name', controlType:'textbox', table:true, label:'Name', required:true}),
      new DomainMetadata({key:'description', controlType:'textbox', table:true, label:'Description', required:true}),
    ];
  }

  getTableUrl(): string {
    return 'items';
  }

  getDetailUrl(): string {
    return '/item';
  }

  getPropertiesLables(): string {
    return undefined;
  }

  getTableTitle(): string {
    return 'Items';
  }

}
