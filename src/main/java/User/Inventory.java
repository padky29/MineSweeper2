package User;

import Game.PowerUp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Inventory {
    private List<PowerUp> inventory;

    public Inventory() {
        inventory = new ArrayList<>();
    }

    public void addPowerUp(PowerUp powerUp, int quantity) {
        for (int i = 0; i < quantity; i++) {
            inventory.add(powerUp);
        }
    }

    public boolean usePowerUp(PowerUp powerUp) {
        if (inventory.contains(powerUp)) {
            inventory.remove(powerUp);
            return true;
        }
        return false;
    }

    public void initializeRandomPowerUps() {
        Random random = new Random();
        // Add 3 random power-ups to the inventory
        for (int i = 0; i < 3; i++) {
            PowerUp.Type type = PowerUp.Type.values()[random.nextInt(PowerUp.Type.values().length)];
            PowerUp newPowerUp = new PowerUp(type); // Assuming Game.PowerUp has a constructor that takes a type
            addPowerUp(newPowerUp, 1); // Add 1 instance of the randomly selected power-up
        }
    }

    public void showInventory() {
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty!");
        } else {
            // Count occurrences of each power-up
            for (PowerUp.Type type : PowerUp.Type.values()) {
                long count = inventory.stream().filter(powerUp -> powerUp.getType() == type).count();
                if (count > 0) {
                    System.out.println(type + ": " + count);
                }
            }
        }
    }

    public boolean isEmpty() {
        return inventory.isEmpty();
    }
}
