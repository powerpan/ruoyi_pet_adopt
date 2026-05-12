import Vue from "vue";

export const appSoundModeBus = new Vue();

const KEY = "ruoyi-app-sound-mode";
const LEGACY_BGM_KEY = "ruoyi-app-bgm-playing";

/** @returns {'none'|'music'|'video'} */
export function readAppSoundMode() {
  try {
    const v = localStorage.getItem(KEY);
    if (v === "music" || v === "video" || v === "none") {
      return v;
    }
    if (localStorage.getItem(LEGACY_BGM_KEY) === "0") {
      return "none";
    }
    return "music";
  } catch (e) {
    return "none";
  }
}

/** @param {'none'|'music'|'video'} mode */
export function notifyAppSoundMode(mode) {
  try {
    localStorage.setItem(KEY, mode);
    localStorage.setItem(LEGACY_BGM_KEY, mode === "music" ? "1" : "0");
  } catch (e) {
    /* ignore */
  }
  appSoundModeBus.$emit("update", mode);
}
