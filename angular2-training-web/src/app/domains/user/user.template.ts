import {BaseDomainTemplate} from "../../common/templates/baseDomain.template";
import {User} from "./user";
import {DomainMetadata} from "../../common/templates/form-metadata";
import {Injectable} from "@angular/core";
/**
 * Created by zemo on 24/02/2017.
 */

@Injectable()
export class UserTemplate extends BaseDomainTemplate<User> {

  constructor() {
    super();
  }

  protected initMetadataArray(): DomainMetadata[] {
    return [
      new DomainMetadata({key:'firstName', controlType:'textbox', table:true, label:'First Name', required:true}),
      new DomainMetadata({key:'lastName', controlType:'textbox', table:true, label:'Last Name', required:true}),
      new DomainMetadata({key:'addressId', controlType:'dropdown', table:false, label:'Address ID', required:false}),
    ];
  }

  getDetailUrl(): string {
    return '/user';
  }

  getTableUrl(): string {
    return '/users';
  }

  getPropertiesLables(): string {
    return '';
  }

  getTableTitle(): string {
    return 'Users';
  }

  getDetailTitle(): string {
    return 'User';
  }

}
