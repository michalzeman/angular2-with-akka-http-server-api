import {BaseEntity} from "../../common/entities/baseEntity";
/**
 * Created by zemo on 24/02/2017.
 */
export class User extends BaseEntity {
  public firstName: string;

  public lastName: string;

  public addressId: number;


  constructor(id: number,firstName: string, lastName: string, addressId: number) {
    super();
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.addressId = addressId;
  }
}
