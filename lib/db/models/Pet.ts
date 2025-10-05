// lib/db/models/Pet.ts
import mongoose, { Schema, Model, Types } from "mongoose";

export interface IPet {
  userId: Types.ObjectId;
  name: string;
  hunger: number;
  happiness: number;
  health: number;
  level: number;
  experience: number;
  evolutionStage: "egg" | "baby" | "teen" | "adult" | "elite";
  lastFedAt: Date;
  createdAt: Date;
  updatedAt: Date;
}

const PetSchema = new Schema<IPet>(
  {
    userId: {
      type: Schema.Types.ObjectId,
      ref: "User",
      required: true,
      index: true, // Index for faster queries
    },
    name: {
      type: String,
      required: true,
    },
    hunger: {
      type: Number,
      default: 50,
      min: 0,
      max: 100,
    },
    happiness: {
      type: Number,
      default: 50,
      min: 0,
      max: 100,
    },
    health: {
      type: Number,
      default: 100,
      min: 0,
      max: 100,
    },
    level: {
      type: Number,
      default: 1,
      min: 1,
    },
    experience: {
      type: Number,
      default: 0,
      min: 0,
    },
    evolutionStage: {
      type: String,
      enum: ["egg", "baby", "teen", "adult", "elite"],
      default: "egg",
    },
    lastFedAt: {
      type: Date,
      default: Date.now,
    },
  },
  {
    timestamps: true,
  }
);

const Pet: Model<IPet> =
  mongoose.models.Pet || mongoose.model<IPet>("Pet", PetSchema);

export default Pet;
