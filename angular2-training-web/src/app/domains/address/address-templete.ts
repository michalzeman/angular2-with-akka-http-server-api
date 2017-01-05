import {BaseDomainTemplate} from "../../common/templates/baseDomain.template";
import {Address} from "./address";
import {DomainMetadata, FormMetadata} from "../../common/templates/form-metadata";
import {Injectable} from "@angular/core";

@Injectable()
export class AddressTemplate extends BaseDomainTemplate<Address> {

  protected initMetadataArray(): DomainMetadata[] {
    return [
      new DomainMetadata({key:'houseNumber', controlType:'textbox', table:true, label:'House Number', required:true}),
      new DomainMetadata({key:'city', controlType:'textbox', table:true, label:'City', required:true}),
      new DomainMetadata({key:'zip', controlType:'textbox', table:true, label:'Zip', required:true}),
      new DomainMetadata({key:'street', controlType:'textbox', table:true, label:'Street', required:true}),
    ];
  }

  getDetailTitle(): string {
    return 'Address';
  }

  getTableUrl(): string {
    return '/addresses';
  }

  getDetailUrl(): string {
    return '/address';
  }

  getPropertiesLables(): string {
    return undefined;
  }

  getTableTitle(): string {
    return 'Addresses';
  }
}
