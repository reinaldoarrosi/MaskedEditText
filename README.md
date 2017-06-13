# MaskedEditText for Android

This is a masked edit text widget for Android. It handles copy and paste, cursor positioning without being super slow or doing complicated things.

## Installation

### Gradle

```groovy
compile 'com.github.reinaldoarrosi:maskededittext:1.1.0'
```

### Maven

```xml
<dependency>
  <groupId>com.github.reinaldoarrosi</groupId>
  <artifactId>maskededittext</artifactId>
  <version>1.1.0</version>
  <type>pom</type>
</dependency>
```

## Usage
MaskedEditText is, as you might have imagined, a class that inherits from EditText. This means that every thing you can do with a EditText you can with a MaskedEditText. Besides that, there are only 5 extra methods:

#### setMask(String mask)
This is used to set a mask. MaskedEditText will try to match the current text with the new mask, so if you change the mask and your text is still valid you'll not lose it.

#### getMask()
This method returns the current mask.

#### setPlaceholder(char placeholder)
This is used to set the placeholder character. This character is shown where an input is expected an by default the placeholder char is a white-space

#### getPlaceholder()
This method returns the current character that is being used as the placeholder char.

#### getText(boolean removeMask)
This method is exaclty like getText() except that you're able to pass a parameter that will determine if the value returned will contain the mask characters or not.

## Mask
The mask is a simple sequence of character where some of these have a special meaning. Any character that does not have a special meaning will be treated as a literal character and will appear as is in the MaskedEditText.

These are the chars that have a special meaning within the mask:
- 9: Numeric mask (this will accept only numbers to be typed)
- A: Alpha mask (this will accept only alphabetic letters to be typed)
- *: Alphanumeric (this will accept numbers and alphabetic letters to be typed)
- ?: Character mask (this will accept anything to be typed)

Examples:
- phone mask: (999) 999-9999
- money: $999,999,999.99
- random valid mask: (A)?*99A-9++*??

If you need one of these special chars to be treated as a literal char you can use the escape char '\' in front of it:
 
Example: Suppose that in the phone mask we need to add a preceding digit that will always be 9, to do this we can change the mask like this:
- phone mask: \9(999) 999-9999

If you need to display the escape char as a literal char just double the escape char like this:
- phone mask: \\\\(999) 999-9999
