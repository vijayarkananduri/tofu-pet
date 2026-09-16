// Global Pet State Machine
const PetState = {
    IDLE: 'idle',
    POKE: 'poke',
    LISTENING: 'listening',
    ANGRY: 'angry',
    DIZZY: 'dizzy',
    ANNOYED: 'annoyed',
    GLOW: 'glow',
    COLD: 'cold',
    LOVED: 'loved'
};

const MoodLevel = {
    WARM: 'warm',
    NEUTRAL: 'neutral',
    COLD: 'cold'
};

class TofuPet {
    constructor() {
        this.currentState = PetState.IDLE;
        this.moodLevel = MoodLevel.NEUTRAL;
        this.isMuted = false;
        this.userName = '';
        this.taskCount = 0;
        this.completedToday = 0;
        this.trustScore = 3;
        this.lastStateChangeTime = Date.now();
        this.stateTimeout = null;
        this.tapCount = 0;
        this.lastTapTime = 0;

        this.petElement = document.querySelector('.pet');
        this.taskBubble = document.getElementById('taskBubble');
        this.speechBubble = document.getElementById('speechBubble');
        this.speechText = document.getElementById('speechText');
        this.taskTitle = document.getElementById('taskTitle');

        this.initEventListeners();
    }

    initEventListeners() {
        document.getElementById('doneBtn')?.addEventListener('click', () => this.checkTask());
        document.getElementById('snoozeBtn')?.addEventListener('click', () => this.snoozeTask());
        document.getElementById('moreBtn')?.addEventListener('click', () => this.openApp());
    }

    setState(newState, duration = 3000) {
        if (this.currentState === newState) return;

        this.petElement.classList.remove(this.currentState);
        this.currentState = newState;
        this.petElement.classList.add(this.currentState);
        this.lastStateChangeTime = Date.now();

        if (this.stateTimeout) clearTimeout(this.stateTimeout);

        if (duration > 0 && newState !== PetState.LISTENING && newState !== PetState.GLOW) {
            this.stateTimeout = setTimeout(() => {
                this.setState(PetState.IDLE, 0);
            }, duration);
        }
    }

    setMood(mood) {
        this.moodLevel = mood;
    }

    // Gesture handlers
    onTap() {
        this.tapCount++;
        const now = Date.now();
        const timeSinceLastTap = now - this.lastTapTime;
        this.lastTapTime = now;

        if (timeSinceLastTap > 300) {
            this.tapCount = 1;
        }

        if (this.tapCount === 1) {
            setTimeout(() => {
                if (this.tapCount === 1) {
                    this.singleTap();
                } else if (this.tapCount === 2) {
                    this.doubleTap();
                } else if (this.tapCount >= 3) {
                    this.tripleTap();
                }
                this.tapCount = 0;
            }, 300);
        }
    }

    singleTap() {
        if (this.currentState === PetState.GLOW) {
            this.openTaskBubble();
        } else {
            this.setState(PetState.POKE, 800);
            this.wiggleEyes();
        }
    }

    doubleTap() {
        this.setState(PetState.POKE, 800);
        this.wiggleMouth();
    }

    tripleTap() {
        this.setState(PetState.ANNOYED, 3000);
        this.speak('That tickles!');
    }

    onShake(level) {
        if (level >= 1 && level <= 5) {
            this.setState(PetState.ANGRY, level * 400);
            this.speak('Stop!');
        } else if (level >= 6) {
            this.setState(PetState.DIZZY, level * 600);
            this.speak('Wheee!');
        }
    }

    onSpin() {
        this.setState(PetState.DIZZY, 3000);
        this.speak('Dizzy!');
    }

    onTilt(x, y) {
        // TODO: Implement tilt animation
    }

    onSoundLevel(level) {
        // React to ambient sound level
    }

    onMood(mood) {
        this.setMood(mood);
    }

    onReminder(taskId, title, count) {
        this.taskCount = count;
        this.currentTaskId = taskId;
        this.setState(PetState.GLOW, 0);
        this.taskTitle.textContent = title;
        this.speak(`You have a task: ${title}`);
    }

    onMuteChanged(muted) {
        this.isMuted = muted;
        if (muted) {
            this.zipMouth();
        } else {
            this.unzipMouth();
        }
    }

    onUserName(name) {
        this.userName = name;
    }

    onIdle() {
        this.setState(PetState.IDLE, 0);
    }

    // Animations
    wiggleEyes() {
        const eyes = document.querySelectorAll('.pet-eye');
        eyes.forEach((eye, i) => {
            eye.style.animation = `none`;
            setTimeout(() => {
                eye.style.animation = `blink 0.3s ease`;
            }, 10);
        });
    }

    wiggleMouth() {
        const mouth = document.querySelector('.pet-mouth');
        mouth.style.animation = `none`;
        setTimeout(() => {
            mouth.style.animation = `wiggle 0.4s ease`;
        }, 10);
    }

    zipMouth() {
        const mouth = document.querySelector('.pet-mouth');
        mouth.setAttribute('d', 'M 70 60 L 70 60 M 80 60 L 80 60 M 90 60 L 90 60 M 100 60 L 100 60');
    }

    unzipMouth() {
        const mouth = document.querySelector('.pet-mouth');
        mouth.setAttribute('d', 'M 70 60 Q 90 70 110 60');
    }

    enableBlush() {
        const blushes = document.querySelectorAll('.pet-blush');
        blushes.forEach(blush => {
            blush.style.opacity = '0.6';
        });
    }

    disableBlush() {
        const blushes = document.querySelectorAll('.pet-blush');
        blushes.forEach(blush => {
            blush.style.opacity = '0';
        });
    }

    openTaskBubble() {
        this.taskBubble.classList.add('show');
    }

    closeTaskBubble() {
        this.taskBubble.classList.remove('show');
    }

    speak(text) {
        if (this.isMuted) return;

        this.speechText.textContent = text;
        this.speechBubble.classList.add('show');

        if (window.AndroidBridge) {
            AndroidBridge.requestTts(text);
        }

        setTimeout(() => {
            this.speechBubble.classList.remove('show');
        }, 3000);
    }

    checkTask() {
        if (window.AndroidBridge) {
            AndroidBridge.checkTask(this.currentTaskId);
        }
        this.setState(PetState.IDLE, 2000);
        this.enableBlush();
        this.petElement.classList.add('bounce');
        this.speak('Great job!');
        this.closeTaskBubble();
        setTimeout(() => this.disableBlush(), 3000);
    }

    snoozeTask() {
        if (window.AndroidBridge) {
            AndroidBridge.snoozeTask(this.currentTaskId);
        }
        this.speak('I\'ll remind you soon.');
        this.closeTaskBubble();
    }

    openApp() {
        if (window.AndroidBridge) {
            AndroidBridge.openApp();
        }
    }
}

// Initialize pet
const pet = new TofuPet();

// Expose to Android bridge
window.Pet = {
    onTap: () => pet.onTap(),
    onShake: (level) => pet.onShake(level),
    onSpin: () => pet.onSpin(),
    onTilt: (x, y) => pet.onTilt(x, y),
    onSoundLevel: (level) => pet.onSoundLevel(level),
    onMood: (mood) => pet.onMood(mood),
    onReminder: (taskId, title, count) => pet.onReminder(taskId, title, count),
    onMuteChanged: (muted) => pet.onMuteChanged(muted),
    onUserName: (name) => pet.onUserName(name),
    onIdle: () => pet.onIdle()
};
