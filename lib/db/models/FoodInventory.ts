// lib/db/models/FoodInventory.ts
import mongoose, { Schema, Model, Types, Document } from "mongoose";

// TypeScript Interface
export interface IFoodInventory extends Document {
  _id: Types.ObjectId;
  userId: Types.ObjectId;
  apples: number;
  steaks: number;
  berries: number;
  biscuits: number;
  createdAt: Date;
  updatedAt: Date;
}

// Type for food types (useful for functions)
export type FoodTypes = "apples" | "steaks" | "berries" | "biscuits";

// Mongoose Schema
const FoodInventorySchema = new Schema<IFoodInventory>(
  {
    userId: {
      type: Schema.Types.ObjectId,
      ref: "User",
      required: true,
      unique: true, // Each user has exactly ONE inventory
      index: true,
    },
    apples: {
      type: Number,
      default: 0,
      min: 0,
      validate: {
        validator: Number.isInteger,
        message: "{VALUE} is not an integer",
      },
    },
    steaks: {
      type: Number,
      default: 0,
      min: 0,
      validate: {
        validator: Number.isInteger,
        message: "{VALUE} is not an integer",
      },
    },
    berries: {
      type: Number,
      default: 0,
      min: 0,
      validate: {
        validator: Number.isInteger,
        message: "{VALUE} is not an integer",
      },
    },
    biscuits: {
      type: Number,
      default: 0,
      min: 0,
      validate: {
        validator: Number.isInteger,
        message: "{VALUE} is not an integer",
      },
    },
  },
  {
    timestamps: true,
  }
);

// Instance method: Add food to inventory
FoodInventorySchema.methods.addFood = function (
  foodType: FoodTypes,
  amount: number
) {
  this[foodType] += amount;
  return this.save();
};

// Instance method: Remove food from inventory (e.g., when feeding pet)
FoodInventorySchema.methods.removeFood = function (
  foodType: FoodTypes,
  amount: number
) {
  if (this[foodType] < amount) {
    throw new Error(
      `Not enough ${foodType}. Have: ${this[foodType]}, Need: ${amount}`
    );
  }
  this[foodType] -= amount;
  return this.save();
};

// Static method: Get or create inventory for user
FoodInventorySchema.statics.getOrCreate = async function (
  userId: Types.ObjectId
) {
  let inventory = await this.findOne({ userId });

  if (!inventory) {
    inventory = await this.create({ userId });
  }

  return inventory;
};

// Model export with hot-reload protection
const FoodInventory: Model<IFoodInventory> =
  mongoose.models.FoodInventory ||
  mongoose.model<IFoodInventory>("FoodInventory", FoodInventorySchema);

export default FoodInventory;
