# Tofu Pet — Contribution Guide

Thanks for your interest in contributing to Tofu Pet! Here's how to get started.

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/tofu-pet.git
   cd tofu-pet
   ```
3. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```
4. **Open in Android Studio** and make your changes
5. **Commit with clear messages**:
   ```bash
   git commit -am 'Add: description of your feature'
   ```
6. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```
7. **Open a Pull Request** on the main repository

## Code Style

- Use **Kotlin** for all new code
- Follow [Google's Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
- Use **4 spaces** for indentation
- Keep functions small and focused
- Add comments for complex logic
- Name variables and functions clearly

## Testing

- Test on **Android 9+** (API 28+)
- Test on both emulator and physical device
- Verify **overlay works** on various OEMs (Samsung, Google Pixel, OnePlus, etc.)
- Check **battery drain** doesn't exceed 5%/hour
- Test **voice commands** in quiet and noisy environments

## Commit Message Format

```
[Type]: Short description (50 chars max)

Optional longer description explaining the change.
Wrap at 72 characters.

Closes #123
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `refactor`: Code refactoring
- `perf`: Performance improvement
- `docs`: Documentation update
- `style`: Code style change (formatting, etc.)
- `test`: Test addition
- `chore`: Build, dependencies, or tooling

## Pull Request Guidelines

1. **Describe your changes clearly** in the PR description
2. **Reference related issues** (e.g., "Closes #42")
3. **Keep PRs focused** on one feature or bug fix
4. **Add tests** for new functionality
5. **Update documentation** if needed
6. **Ensure CI passes** (no build errors)

## Issues & Discussions

- **Bug Reports**: Use the Issue template, include device info and reproduction steps
- **Feature Requests**: Describe the use case and why Tofu needs it
- **Questions**: Post in [Discussions](https://github.com/vijayarkananduri/tofu-pet/discussions)

## Areas for Contribution

### High Priority
- 🐛 Bug fixes
- ⚡ Performance optimizations
- 🧪 Unit/integration tests
- 📝 Documentation improvements

### Medium Priority
- 🎨 UI polish and animations
- 🌍 Internationalization (i18n)
- ♿ Accessibility improvements
- 📊 Analytics (privacy-respecting)

### Nice-to-Have
- 🔊 Additional sound packs
- 🎭 More mood animations
- 🎮 Gesture customization
- 📱 Tablet/landscape support

## Development Setup

### Requirements
- Android Studio Giraffe (2022.3.1)+
- Android SDK 34
- Kotlin 1.9.10+
- Gradle 8.1+

### Build Variants
- `debug`: Development build with logging
- `release`: Optimized release build

### Running Tests
```bash
./gradlew test              # Unit tests
./gradlew connectedAndroidTest  # Instrumented tests
```

## Code Review Process

1. A maintainer will review your PR
2. Feedback will be provided within 48 hours
3. Make requested changes
4. Request re-review when ready
5. Merge once approved ✨

## Community Guidelines

- Be respectful and constructive
- Assume good intent
- Welcome newcomers
- Give credit generously
- Have fun! 🎉

## Questions?

Feels free to ask in:
- [GitHub Discussions](https://github.com/vijayarkananduri/tofu-pet/discussions)
- [GitHub Issues](https://github.com/vijayarkananduri/tofu-pet/issues)

---

**Happy coding! Tofu appreciates your contribution.** 🍲
