package gnu.maseckt.linuxAuthenticator.models;

import java.util.UUID;

public class PlayerAttempts {
    private final UUID playerId;
    private int attempts;
    private boolean banned;
    private long banExpireTime;
    
    public PlayerAttempts(UUID playerId) {
        this.playerId = playerId;
        this.attempts = 0;
        this.banned = false;
        this.banExpireTime = 0;
    }
    
    public UUID getPlayerId() {
        return playerId;
    }
    
    public int getAttempts() {
        return attempts;
    }
    
    public void incrementAttempts() {
        this.attempts++;
    }
    
    public boolean isBanned() {
        if (banned && banExpireTime > 0) {
            if (System.currentTimeMillis() >= banExpireTime) {
                banned = false;
                banExpireTime = 0;
                return false;
            }
            return true;
        }
        return banned;
    }
    
    public void setBanned(boolean banned) {
        this.banned = banned;
        if (!banned) {
            this.banExpireTime = 0;
        }
    }
    
    public void setBannedWithDuration(boolean banned, long durationMinutes) {
        this.banned = banned;
        if (banned) {
            this.banExpireTime = System.currentTimeMillis() + (durationMinutes * 60 * 1000);
        } else {
            this.banExpireTime = 0;
        }
    }
    
    public long getBanExpireTime() {
        return banExpireTime;
    }
    
    public long getRemainingBanTime() {
        if (!isBanned()) {
            return 0;
        }
        return Math.max(0, banExpireTime - System.currentTimeMillis());
    }
    
    public void reset() {
        this.attempts = 0;
        this.banned = false;
        this.banExpireTime = 0;
    }
} 