import {BaseEntity} from "../../common/entities/baseEntity";
import {Item} from "../item/Item";
export interface Test extends BaseEntity {
  name: string;
  list?: Item
}
