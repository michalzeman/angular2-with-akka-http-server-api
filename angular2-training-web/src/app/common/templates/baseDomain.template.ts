import {DomainMetadata} from "./form-metadata";
import {BaseEntity} from "../entities/baseEntity";

export abstract class BaseDomainTemplate<E extends BaseEntity> {

  metadataArray: DomainMetadata[];

  constructor() {
    this.metadataArray = this.initMetadataArray();
  }

  protected abstract initMetadataArray(): DomainMetadata[];

  abstract getDetailUrl():string;

  abstract getTableUrl():string;

  abstract getPropertiesLables():string;

  abstract getTableTitle(): string;
}
