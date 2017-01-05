import {BaseEntity} from "../../common/entities/baseEntity";
export class Address extends BaseEntity {
  houseNumber: string;
  city: string;
  zip: string;
  street: string;
}
