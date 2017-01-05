import {BaseEntityServiceImpl} from "../../common/services/entity/base-entity.service";
import {Address} from "./address";
import {DelegateService} from "../../common/services/delegate.service";
import {Http} from "@angular/http";
import {Injectable} from "@angular/core";

@Injectable()
export class AddressService extends BaseEntityServiceImpl<Address> {

  constructor(delegateService: DelegateService, http: Http) {
    super(delegateService, http, '/addresses');
  }
}
