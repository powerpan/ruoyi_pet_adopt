<template>
  <div class="bgm-root">
    <div
      class="bgm-main-wrap"
      role="button"
      tabindex="0"
      :class="{ 'bgm-main-wrap--lit': lit }"
      @mouseenter="lit = true"
      @mouseleave="lit = false"
      @focus="lit = true"
      @blur="lit = false"
      :title="mainTitle"
      @click.stop="toggleMenu"
      @keydown.enter.prevent="toggleMenu"
      @keydown.space.prevent="toggleMenu"
    >
      <div class="bgm-disc" :class="{ spinning }" aria-hidden="true">
        <span class="bgm-groove" />
        <span class="bgm-icon">♪</span>
      </div>
      <span class="bgm-label">{{ mainLabel }}</span>
    </div>
    <transition name="bgm-slide">
      <div v-if="menuOpen" class="bgm-options" @click.stop>
        <div
          class="bgm-option"
          role="button"
          tabindex="0"
          :class="{ 'bgm-option--active': soundMode === 'music' }"
          title="背景音乐"
          @click="onPickMusic"
          @keydown.enter.prevent="onPickMusic"
        >
          <div class="bgm-option-disc" :class="{ spinning: soundMode === 'music' && bgmPlaying }">
            <span class="bgm-groove" />
            <span class="bgm-option-icon">♫</span>
          </div>
          <span class="bgm-option-label">音乐</span>
        </div>
        <div
          class="bgm-option"
          role="button"
          tabindex="0"
          :class="{ 'bgm-option--active': soundMode === 'video' }"
          title="首页轮播视频原声"
          @click="onPickVideo"
          @keydown.enter.prevent="onPickVideo"
        >
          <div class="bgm-option-disc" :class="{ spinning: soundMode === 'video' }">
            <span class="bgm-groove" />
            <span class="bgm-option-icon">▶</span>
          </div>
          <span class="bgm-option-label">视频原声</span>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { BGM_PUBLIC_PATH } from "@/config/bgm";
import { readAppSoundMode, notifyAppSoundMode, appSoundModeBus } from "@/utils/appSoundMode";

export default {
  name: "BgmPlayer",
  data() {
    return {
      menuOpen: false,
      lit: false,
      soundMode: "none",
      bgmPlaying: false,
    };
  },
  computed: {
    spinning() {
      if (this.soundMode === "music" && this.bgmPlaying) {
        return true;
      }
      if (this.soundMode === "video") {
        return true;
      }
      return false;
    },
    mainLabel() {
      if (this.soundMode === "music") {
        return "音乐";
      }
      if (this.soundMode === "video") {
        return "视频原声";
      }
      return "音效";
    },
    mainTitle() {
      if (this.menuOpen) {
        return "收起";
      }
      return "选择音乐或视频原声";
    },
  },
  mounted() {
    const base = (process.env.BASE_URL || "/").replace(/\/?$/, "/");
    const path = base + BGM_PUBLIC_PATH;
    this.audio = new Audio(path);
    this.audio.loop = true;
    this.audio.preload = "auto";
    const onPlay = () => {
      this.bgmPlaying = true;
    };
    const onPause = () => {
      this.bgmPlaying = false;
    };
    this.audio.addEventListener("play", onPlay);
    this.audio.addEventListener("pause", onPause);
    this._onPlay = onPlay;
    this._onPause = onPause;

    this.soundMode = readAppSoundMode();
    this.applySoundMode(this.soundMode, false);
    appSoundModeBus.$on("update", this._syncSoundModeFromBus);
  },
  beforeDestroy() {
    appSoundModeBus.$off("update", this._syncSoundModeFromBus);
    if (this.audio) {
      this.audio.removeEventListener("play", this._onPlay);
      this.audio.removeEventListener("pause", this._onPause);
      this.audio.pause();
      this.audio.src = "";
      this.audio = null;
    }
  },
  methods: {
    _syncSoundModeFromBus(mode) {
      if (mode === this.soundMode) {
        return;
      }
      this.applySoundMode(mode, false);
    },
    toggleMenu() {
      this.menuOpen = !this.menuOpen;
    },
    closeMenu() {
      this.menuOpen = false;
    },
    applySoundMode(mode, persist) {
      this.soundMode = mode;
      if (persist) {
        notifyAppSoundMode(mode);
      }
      if (!this.audio) {
        return;
      }
      if (mode === "music") {
        this.audio
          .play()
          .then(() => {})
          .catch(() => {});
      } else {
        this.audio.pause();
      }
    },
    onPickMusic() {
      if (this.soundMode === "music") {
        this.applySoundMode("none", true);
      } else {
        this.applySoundMode("music", true);
      }
      this.closeMenu();
    },
    onPickVideo() {
      if (this.soundMode === "video") {
        this.applySoundMode("none", true);
      } else {
        this.applySoundMode("video", true);
      }
      this.closeMenu();
    },
  },
};
</script>

<style lang="scss" scoped>
.bgm-root {
  position: fixed;
  left: 22px;
  bottom: 28px;
  z-index: 10050;
  display: flex;
  flex-direction: row;
  align-items: flex-end;
  gap: 14px;
  user-select: none;
}

.bgm-main-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #0f172a;
  opacity: 0.3;
  transition: opacity 0.25s ease;
}

.bgm-main-wrap--lit {
  opacity: 1;
}

.bgm-options {
  display: flex;
  flex-direction: row;
  align-items: flex-end;
  gap: 12px;
  padding-bottom: 2px;
}

.bgm-slide-enter-active,
.bgm-slide-leave-active {
  transition: opacity 0.22s ease, transform 0.22s ease;
}

.bgm-slide-enter,
.bgm-slide-leave-to {
  opacity: 0;
  transform: translateX(-16px);
}

.bgm-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #0f172a;
  opacity: 0.85;
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.bgm-option:hover,
.bgm-option:focus {
  opacity: 1;
  outline: none;
}

.bgm-option--active .bgm-option-disc {
  border-color: rgba(96, 165, 250, 0.85);
  box-shadow: 0 8px 22px rgba(47, 117, 255, 0.4), inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

.bgm-option-disc {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(145deg, #1e293b 0%, #334155 45%, #0f172a 100%);
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.32), inset 0 1px 0 rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.bgm-option-icon {
  font-size: 16px;
  color: #93c5fd;
  text-shadow: 0 0 10px rgba(147, 197, 253, 0.45);
  line-height: 1;
}

.bgm-option-label {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.03em;
  color: #475569;
  text-shadow: 0 1px 0 rgba(255, 255, 255, 0.9);
  padding: 1px 6px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(226, 232, 240, 0.9);
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.06);
  white-space: nowrap;
}

.bgm-disc {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: linear-gradient(145deg, #1e293b 0%, #334155 45%, #0f172a 100%);
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.35), inset 0 1px 0 rgba(255, 255, 255, 0.12);
  border: 2px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.bgm-main-wrap--lit .bgm-disc {
  box-shadow: 0 10px 28px rgba(47, 117, 255, 0.35), inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

.bgm-groove {
  position: absolute;
  inset: 8px;
  border-radius: 50%;
  border: 1px dashed rgba(255, 255, 255, 0.12);
  pointer-events: none;
}

.bgm-option-disc .bgm-groove {
  inset: 6px;
}

.bgm-icon {
  font-size: 22px;
  color: #93c5fd;
  text-shadow: 0 0 12px rgba(147, 197, 253, 0.5);
  line-height: 1;
}

.bgm-disc.spinning,
.bgm-option-disc.spinning {
  animation: bgm-spin 10s linear infinite;
}

@keyframes bgm-spin {
  to {
    transform: rotate(360deg);
  }
}

.bgm-label {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.04em;
  color: #475569;
  text-shadow: 0 1px 0 rgba(255, 255, 255, 0.9);
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(226, 232, 240, 0.95);
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
}
</style>
