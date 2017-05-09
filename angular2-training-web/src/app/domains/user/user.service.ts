/**
 * Created by zemo on 24/02/2017.
 */
import {BaseEntityServiceImpl} from "../../common/services/entity/base-entity.service";
import {User} from "./user";
import {Injectable} from "@angular/core";
import {DelegateService} from "../../common/services/delegate.service";
import {Http} from "@angular/http";

@Injectable()
export class UserService extends BaseEntityServiceImpl<User> {

  constructor(delegateService: DelegateService, http: Http) {
    super(delegateService, http, '/users');
  }

}
