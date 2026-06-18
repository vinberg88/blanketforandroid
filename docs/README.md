# Blanket for Android - Documentation

This directory contains comprehensive documentation for the Blanket for Android project.

## 📚 Documentation Files

### User Documentation

- **[BUILD_NOTES.md](BUILD_NOTES.md)** - Build instructions, project structure, and implemented features
  - Project setup and structure
  - Building the project in Android Studio or command line
  - Dependencies and requirements
  - Known limitations

- **[AZURE_CICD.md](AZURE_CICD.md)** - Azure Pipelines CI/CD for building APK files
  - Setting up Azure Pipelines
  - Automated APK builds (debug, internal, release)
  - Downloading build artifacts
  - Configuring release signing
  - Troubleshooting builds

- **[TESTING_GUIDE.md](TESTING_GUIDE.md)** - Step-by-step instructions for testing the app
  - Prerequisites
  - Build instructions
  - Manual testing procedures
  - Feature testing checklist

- **[PLAY_STORE_RELEASE.md](PLAY_STORE_RELEASE.md)** - Google Play release checklist
  - Target SDK 35 notes
  - Privacy Policy and Data Safety guidance
  - Release signing and Android App Bundle build
  - Dependabot/security checklist

### Developer Documentation

- **[IMPLEMENTATION.md](IMPLEMENTATION.md)** - Technical architecture and design decisions
  - Architecture overview (MVVM)
  - Component descriptions
  - Data flow diagrams
  - Performance considerations
  - Key design decisions

- **[UI_DESIGN.md](UI_DESIGN.md)** - Visual design reference and theming
  - Color scheme and theming
  - Component specifications
  - Layout structure
  - Material Design guidelines

- **[SUMMARY.md](SUMMARY.md)** - Implementation summary
  - What was implemented
  - Code statistics
  - Architecture highlights
  - Acceptance criteria status

### Development Resources

- **[development/PR_OVERVIEW.md](development/PR_OVERVIEW.md)** - Pull request guidelines
  - PR structure and changes
  - Feature overview
  - Documentation references

## 🗂️ Documentation Structure

```
docs/
├── README.md                    # This file
├── BUILD_NOTES.md              # Build instructions
├── AZURE_CICD.md               # Azure Pipelines CI/CD
├── IMPLEMENTATION.md           # Technical architecture
├── PLAY_STORE_RELEASE.md       # Google Play launch checklist
├── play-store/                 # Store listing text and feature graphic
├── TESTING_GUIDE.md            # Testing procedures
├── UI_DESIGN.md                # Design reference
├── SUMMARY.md                  # Implementation summary
├── GRADLE_CONFIGURATION.md     # Gradle configuration
└── development/
    └── PR_OVERVIEW.md          # PR guidelines
```

## 🚀 Quick Start

1. **New to the project?** Start with [BUILD_NOTES.md](BUILD_NOTES.md)
2. **Want to build locally?** See [BUILD_NOTES.md](BUILD_NOTES.md) or [TESTING_GUIDE.md](TESTING_GUIDE.md)
3. **Want to build with Azure?** See [AZURE_CICD.md](AZURE_CICD.md)
4. **Preparing Google Play?** Use [PLAY_STORE_RELEASE.md](PLAY_STORE_RELEASE.md)
5. **Understanding the code?** Read [IMPLEMENTATION.md](IMPLEMENTATION.md)
6. **Working on UI?** Check [UI_DESIGN.md](UI_DESIGN.md)
7. **Contributing?** See [development/PR_OVERVIEW.md](development/PR_OVERVIEW.md)

## 📖 Related Documentation

- **[Main README](../README.md)** - Project overview and quick start guide
- **[License](../LICENSE)** - Project license (MIT)
