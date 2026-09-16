// Tofu's small state machine: visual-first, offline, and bridge-compatible.
const PetState = { IDLE: 'idle', POKE: 'poke', LISTENING: 'listening', ANGRY: 'angry', DIZZY: 'dizzy', ANNOYED: 'annoyed', GLOW: 'glow', COLD: 'cold', LOVED: 'loved' };
const MoodLevel = { WARM: 'warm', NEUTRAL: 'neutral', COLD: 'cold' };

class TofuPet {
    constructor() {
        this.currentState = PetState.IDLE;
        this.moodLevel = MoodLevel.NEUTRAL;
        this.isMuted = false;
        this.userName = '';
        this.taskCount = 0;
        this.lastTapTime = 0;
        this.tapCount = 0;
        this.stateTimeout = null;
        this.petElement = document.querySelector('.pet');
        this.island = document.getElementById('island');
        this.taskBubble = document.getElementById('taskBubble');
        this.speechBubble = document.getElementById('speechBubble');
        this.speechText = document.getElementById('speechText');
        this.taskTitle = document.getElementById('taskTitle');
        this.statusText = document.getElementById('statusText');
        this.micIndicator = document.getElementById('micIndicator');
        document.getElementById('doneBtn')?.addEventListener('click', () => this.checkTask());
        document.getElementById('snoozeBtn')?.addEventListener('click', () => this.snoozeTask());
        document.getElementById('moreBtn')?.addEventListener('click', () => this.openApp());
    }

    setState(newState, duration = 3000) {
        this.island.classList.remove(this.currentState);
        this.currentState = newState;
        this.island.classList.add(newState);
        const labels = { idle: 'here with you', poke: 'hey', listening: 'listening…', angry: 'too much', dizzy: 'whoa', annoyed: 'that tickles', glow: 'a little thing', cold: '…', loved: 'you’re doing good' };
        this.statusText.textContent = labels[newState] || 'here with you';
        if (this.stateTimeout) clearTimeout(this.stateTimeout);
        if (duration > 0 && ![PetState.LISTENING, PetState.GLOW].includes(newState)) {
            this.stateTimeout = setTimeout(() => this.setState(this.moodLevel === MoodLevel.COLD ? PetState.COLD : PetState.IDLE, 0), duration);
        }
    }

    setMood(mood) {
        this.moodLevel = mood;
        if (mood === MoodLevel.WARM && this.currentState === PetState.IDLE) this.island.classList.add('happy');
        if (mood === MoodLevel.COLD && this.currentState === PetState.IDLE) this.setState(PetState.COLD, 0);
    }

    onTap() {
        const now = Date.now();
        if (now - this.lastTapTime > 300) this.tapCount = 0;
        this.tapCount += 1;
        this.lastTapTime = now;
        setTimeout(() => {
            if (this.tapCount === 1) this.currentState === PetState.GLOW ? this.openTaskBubble() : this.singleTap();
            else if (this.tapCount === 2) this.doubleTap();
            else if (this.tapCount >= 3) this.tripleTap();
            this.tapCount = 0;
        }, 300);
    }

    singleTap() { this.setState(PetState.POKE, 800); this.wiggleEyes(); this.vibrate(12); }
    doubleTap() { this.setState(PetState.POKE, 800); this.wiggleMouth(); this.vibrate(20); }
    tripleTap() { this.setState(PetState.ANNOYED, 3000); this.speak('That tickles.'); this.vibrate(24); }
    onShake(level) { if (level <= 5) { this.setState(PetState.ANGRY, level * 400); this.speak('Easy.'); } else { this.setState(PetState.DIZZY, level * 600); this.speak('I’m dizzy.'); } this.vibrate(30); }
    onSpin() { this.setState(PetState.DIZZY, 3000); this.speak('Whoa.'); this.vibrate(25); }
    onTilt(x, y) { this.petElement.style.transform = `translate(${Math.max(-4, Math.min(4, x * 2))}px, ${Math.max(-3, Math.min(3, y * 2))}px)`; }
    onSoundLevel() { /* reserved for a future subtle listening ripple */ }
    onMood(mood) { this.setMood(mood); }

    onReminder(taskId, title, count) {
        this.taskCount = count; this.currentTaskId = taskId;
        this.setState(PetState.GLOW, 0); this.taskTitle.textContent = title || 'A little thing for you';
        this.speak('A little thing for you.');
    }

    onMuteChanged(muted) { this.isMuted = muted; this.island.classList.toggle('muted', muted); if (muted) this.zipMouth(); else this.unzipMouth(); }
    onUserName(name) { this.userName = name || ''; }
    onIdle() { this.setState(this.moodLevel === MoodLevel.COLD ? PetState.COLD : PetState.IDLE, 0); }

    wiggleEyes() { this.petElement.querySelectorAll('.pet-eye').forEach(eye => { eye.animate([{ transform: 'scaleY(1)' }, { transform: 'scaleY(.18)' }, { transform: 'scaleY(1)' }], { duration: 280 }); }); }
    wiggleMouth() { this.petElement.querySelector('.pet-mouth')?.animate([{ transform: 'scaleX(1)' }, { transform: 'scaleX(1.25)' }, { transform: 'scaleX(1)' }], { duration: 380 }); }
    zipMouth() { this.petElement.querySelector('.pet-mouth-muted').setAttribute('opacity', '1'); }
    unzipMouth() { this.petElement.querySelector('.pet-mouth-muted').setAttribute('opacity', '0'); }
    enableBlush() { this.island.classList.add('happy'); }
    disableBlush() { this.island.classList.remove('happy'); }
    openTaskBubble() { this.taskBubble.classList.add('show'); }
    closeTaskBubble() { this.taskBubble.classList.remove('show'); }

    speak(text) {
        this.speechText.textContent = text;
        this.speechBubble.classList.add('show');
        if (!this.isMuted && window.AndroidBridge) AndroidBridge.requestTts(text);
        clearTimeout(this.speechTimer);
        this.speechTimer = setTimeout(() => this.speechBubble.classList.remove('show'), 3000);
    }

    checkTask() {
        if (window.AndroidBridge) AndroidBridge.checkTask(this.currentTaskId || '');
        this.setState(PetState.LOVED, 2000); this.enableBlush(); this.speak(this.userName ? `Good job, ${this.userName}.` : 'Good job.'); this.closeTaskBubble();
        setTimeout(() => this.disableBlush(), 3000);
    }
    snoozeTask() { if (window.AndroidBridge) AndroidBridge.snoozeTask(this.currentTaskId || ''); this.speak('Okay. A little later.'); this.closeTaskBubble(); }
    openApp() { if (window.AndroidBridge) AndroidBridge.openApp(); }
    vibrate(ms) { if (window.AndroidBridge) AndroidBridge.vibrate(ms); }
}

const pet = new TofuPet();
window.Pet = {
    onTap: () => pet.onTap(), onShake: level => pet.onShake(level), onSpin: () => pet.onSpin(), onTilt: (x, y) => pet.onTilt(x, y), onSoundLevel: level => pet.onSoundLevel(level), onMood: mood => pet.onMood(mood), onReminder: (taskId, title, count) => pet.onReminder(taskId, title, count), onMuteChanged: muted => pet.onMuteChanged(muted), onUserName: name => pet.onUserName(name), onIdle: () => pet.onIdle()
};
