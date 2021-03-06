import {BaseDomainTemplate} from "../../common/templates/baseDomain.template";
import {Item} from "./Item";
import {Injectable} from "@angular/core";
import {DomainMetadata, FormMetadata} from "../../common/templates/form-metadata";

@Injectable()
export class ItemTemplate extends BaseDomainTemplate<Item> {

  constructor() {
    super();
  }

  protected initMetadataArray(): DomainMetadata[] {
    return [
      new DomainMetadata({key:'name', controlType:'textbox', table:true, label:'Name', required:true}),
      new DomainMetadata({key:'description', controlType:'textbox', table:true, label:'Description', required:true}),
    ];
  }

  getDetailTitle(): string {
    return 'Item';
  }

  getTableUrl(): string {
    return '/items';
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
