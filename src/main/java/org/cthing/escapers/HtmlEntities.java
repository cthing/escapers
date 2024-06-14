/*
 * Copyright 2024 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.cthing.annotations.NoCoverageGenerated;


/**
 * HTML named character entities listed in <a href="https://www.w3.org/TR/html40/sgml/entities.html">Section 24</a>
 * of the HTML 4 specification.
 */
@SuppressWarnings("Java9CollectionFactory")
final class HtmlEntities {

    static final Map<Integer, String> MARKUP_SIGNIFICANT;
    static {
        final Map<Integer, String> entities = new HashMap<>(4);
        entities.put(0x22, "&quot;");       // " - double-quote
        entities.put(0x26, "&amp;");        // & - ampersand
        entities.put(0x3C, "&lt;");         // < - less-than
        entities.put(0x3E, "&gt;");         // > - greater-than
        MARKUP_SIGNIFICANT = Collections.unmodifiableMap(entities);
    }

    static final Map<Integer, String> ISO_LATIN_1;
    static {
        final Map<Integer, String> entities = new HashMap<>(96);
        entities.put(0xA0, "&nbsp;");       // non-breaking space
        entities.put(0xA1, "&iexcl;");      // inverted exclamation mark
        entities.put(0xA2, "&cent;");       // cent sign
        entities.put(0xA3, "&pound;");      // pound sign
        entities.put(0xA4, "&curren;");     // currency sign
        entities.put(0xA5, "&yen;");        // yen sign
        entities.put(0xA6, "&brvbar;");     // broken vertical bar
        entities.put(0xA7, "&sect;");       // section sign
        entities.put(0xA8, "&uml;");        // spacing diaeresis
        entities.put(0xA9, "&copy;");       // copyright sign
        entities.put(0xAA, "&ordf;");       // feminine ordinal indicator
        entities.put(0xAB, "&laquo;");      // left-pointing double angle quotation mark
        entities.put(0xAC, "&not;");        // not sign
        entities.put(0xAD, "&shy;");        // soft hyphen
        entities.put(0xAE, "&reg;");        // registered trademark sign
        entities.put(0xAF, "&macr;");       // macron
        entities.put(0xB0, "&deg;");        // degree sign
        entities.put(0xB1, "&plusmn;");     // plus-minus sign
        entities.put(0xB2, "&sup2;");       // superscript two
        entities.put(0xB3, "&sup3;");       // superscript three
        entities.put(0xB4, "&acute;");      // acute accent
        entities.put(0xB5, "&micro;");      // micro sign
        entities.put(0xB6, "&para;");       // paragraph sign
        entities.put(0xB7, "&middot;");     // middle dot
        entities.put(0xB8, "&cedil;");      // cedilla
        entities.put(0xB9, "&sup1;");       // superscript one
        entities.put(0xBA, "&ordm;");       // masculine ordinal indicator
        entities.put(0xBB, "&raquo;");      // right-pointing double angle quotation mark
        entities.put(0xBC, "&frac14;");     // fraction one quarter
        entities.put(0xBD, "&frac12;");     // fraction one half
        entities.put(0xBE, "&frac34;");     // fraction three quarters
        entities.put(0xBF, "&iquest;");     // inverted question mark
        entities.put(0xC0, "&Agrave;");     // latin capital letter A with grave
        entities.put(0xC1, "&Aacute;");     // latin capital letter A with acute
        entities.put(0xC2, "&Acirc;");      // latin capital letter A with circumflex
        entities.put(0xC3, "&Atilde;");     // latin capital letter A with tilde
        entities.put(0xC4, "&Auml;");       // latin capital letter A with diaeresis
        entities.put(0xC5, "&Aring;");      // latin capital letter A with ring above
        entities.put(0xC6, "&AElig;");      // latin capital letter AE
        entities.put(0xC7, "&Ccedil;");     // latin capital letter C with cedilla
        entities.put(0xC8, "&Egrave;");     // latin capital letter E with grave
        entities.put(0xC9, "&Eacute;");     // latin capital letter E with acute
        entities.put(0xCA, "&Ecirc;");      // latin capital letter E with circumflex
        entities.put(0xCB, "&Euml;");       // latin capital letter E with diaeresis
        entities.put(0xCC, "&Igrave;");     // latin capital letter I with grave
        entities.put(0xCD, "&Iacute;");     // latin capital letter I with acute
        entities.put(0xCE, "&Icirc;");      // latin capital letter I with circumflex
        entities.put(0xCF, "&Iuml;");       // latin capital letter I with diaeresis
        entities.put(0xD0, "&ETH;");        // latin capital letter ETH
        entities.put(0xD1, "&Ntilde;");     // latin capital letter N with tilde
        entities.put(0xD2, "&Ograve;");     // latin capital letter O with grave
        entities.put(0xD3, "&Oacute;");     // latin capital letter O with acute
        entities.put(0xD4, "&Ocirc;");      // latin capital letter O with circumflex
        entities.put(0xD5, "&Otilde;");     // latin capital letter O with tilde
        entities.put(0xD6, "&Ouml;");       // latin capital letter O with diaeresis
        entities.put(0xD7, "&times;");      // multiplication sign
        entities.put(0xD8, "&Oslash;");     // latin capital letter O with stroke
        entities.put(0xD9, "&Ugrave;");     // latin capital letter U with grave
        entities.put(0xDA, "&Uacute;");     // latin capital letter U with acute
        entities.put(0xDB, "&Ucirc;");      // latin capital letter U with circumflex
        entities.put(0xDC, "&Uuml;");       // latin capital letter U with diaeresis
        entities.put(0xDD, "&Yacute;");     // latin capital letter Y with acute
        entities.put(0xDE, "&THORN;");      // latin capital letter THORN
        entities.put(0xDF, "&szlig;");      // latin small letter sharp s
        entities.put(0xE0, "&agrave;");     // latin small letter a with grave
        entities.put(0xE1, "&aacute;");     // latin small letter a with acute
        entities.put(0xE2, "&acirc;");      // latin small letter a with circumflex
        entities.put(0xE3, "&atilde;");     // latin small letter a with tilde
        entities.put(0xE4, "&auml;");       // latin small letter a with diaeresis
        entities.put(0xE5, "&aring;");      // latin small letter a with ring above
        entities.put(0xE6, "&aelig;");      // latin small letter ae
        entities.put(0xE7, "&ccedil;");     // latin small letter c with cedilla
        entities.put(0xE8, "&egrave;");     // latin small letter e with grave
        entities.put(0xE9, "&eacute;");     // latin small letter e with acute
        entities.put(0xEA, "&ecirc;");      // latin small letter e with circumflex
        entities.put(0xEB, "&euml;");       // latin small letter e with diaeresis
        entities.put(0xEC, "&igrave;");     // latin small letter i with grave
        entities.put(0xED, "&iacute;");     // latin small letter i with acute
        entities.put(0xEE, "&icirc;");      // latin small letter i with circumflex
        entities.put(0xEF, "&iuml;");       // latin small letter i with diaeresis
        entities.put(0xF0, "&eth;");        // latin small letter eth
        entities.put(0xF1, "&ntilde;");     // latin small letter n with tilde
        entities.put(0xF2, "&ograve;");     // latin small letter o with grave
        entities.put(0xF3, "&oacute;");     // latin small letter o with acute
        entities.put(0xF4, "&ocirc;");      // latin small letter o with circumflex
        entities.put(0xF5, "&otilde;");     // latin small letter o with tilde
        entities.put(0xF6, "&ouml;");       // latin small letter o with diaeresis
        entities.put(0xF7, "&divide;");     // division sign
        entities.put(0xF8, "&oslash;");     // latin small letter o with stroke
        entities.put(0xF9, "&ugrave;");     // latin small letter u with grave
        entities.put(0xFA, "&uacute;");     // latin small letter u with acute
        entities.put(0xFB, "&ucirc;");      // latin small letter u with circumflex
        entities.put(0xFC, "&uuml;");       // latin small letter u with diaeresis
        entities.put(0xFD, "&yacute;");     // latin small letter y with acute
        entities.put(0xFE, "&thorn;");      // latin small letter thorn
        entities.put(0xFF, "&yuml;");       // latin small letter y with diaeresis
        ISO_LATIN_1 = Collections.unmodifiableMap(entities);
    }

    static final Map<Integer, String> HTML4_EXTENDED;
    static {
        final Map<Integer, String> entities = new HashMap<>(152);
        entities.put(0x152, "&OElig;");     // latin capital ligature OE
        entities.put(0x153, "&oelig;");     // latin small ligature oe
        entities.put(0x160, "&Scaron;");    // latin capital letter S with caron
        entities.put(0x161, "&scaron;");    // latin small letter s with caron
        entities.put(0x178, "&Yuml;");      // latin capital letter Y with diaeresis
        entities.put(0x192, "&fnof;");      // latin small f with hook
        entities.put(0x2C6, "&circ;");      // modifier letter circumflex accent
        entities.put(0x2DC, "&tilde;");     // small tilde
        entities.put(0x391, "&Alpha;");     // greek capital letter alpha
        entities.put(0x392, "&Beta;");      // greek capital letter beta
        entities.put(0x393, "&Gamma;");     // greek capital letter gamma
        entities.put(0x394, "&Delta;");     // greek capital letter delta
        entities.put(0x395, "&Epsilon;");   // greek capital letter epsilon
        entities.put(0x396, "&Zeta;");      // greek capital letter zeta
        entities.put(0x397, "&Eta;");       // greek capital letter eta
        entities.put(0x398, "&Theta;");     // greek capital letter theta
        entities.put(0x399, "&Iota;");      // greek capital letter iota
        entities.put(0x39A, "&Kappa;");     // greek capital letter kappa
        entities.put(0x39B, "&Lambda;");    // greek capital letter lambda
        entities.put(0x39C, "&Mu;");        // greek capital letter mu
        entities.put(0x39D, "&Nu;");        // greek capital letter nu
        entities.put(0x39E, "&Xi;");        // greek capital letter xi
        entities.put(0x39F, "&Omicron;");   // greek capital letter omicron
        entities.put(0x3A0, "&Pi;");        // greek capital letter pi
        entities.put(0x3A1, "&Rho;");       // greek capital letter rho
        entities.put(0x3A3, "&Sigma;");     // greek capital letter sigma
        entities.put(0x3A4, "&Tau;");       // greek capital letter tau
        entities.put(0x3A5, "&Upsilon;");   // greek capital letter upsilon
        entities.put(0x3A6, "&Phi;");       // greek capital letter phi
        entities.put(0x3A7, "&Chi;");       // greek capital letter chi
        entities.put(0x3A8, "&Psi;");       // greek capital letter psi
        entities.put(0x3A9, "&Omega;");     // greek capital letter omega
        entities.put(0x3B1, "&alpha;");     // greek small letter alpha
        entities.put(0x3B2, "&beta;");      // greek small letter beta
        entities.put(0x3B3, "&gamma;");     // greek small letter gamma
        entities.put(0x3B4, "&delta;");     // greek small letter delta
        entities.put(0x3B5, "&epsilon;");   // greek small letter epsilon
        entities.put(0x3B6, "&zeta;");      // greek small letter zeta
        entities.put(0x3B7, "&eta;");       // greek small letter eta
        entities.put(0x3B8, "&theta;");     // greek small letter theta
        entities.put(0x3B9, "&iota;");      // greek small letter iota
        entities.put(0x3BA, "&kappa;");     // greek small letter kappa
        entities.put(0x3BB, "&lambda;");    // greek small letter lambda
        entities.put(0x3BC, "&mu;");        // greek small letter mu
        entities.put(0x3BD, "&nu;");        // greek small letter nu
        entities.put(0x3BE, "&xi;");        // greek small letter xi
        entities.put(0x3BF, "&omicron;");   // greek small letter omicron
        entities.put(0x3C0, "&pi;");        // greek small letter pi
        entities.put(0x3C1, "&rho;");       // greek small letter rho
        entities.put(0x3C2, "&sigmaf;");    // greek small letter final sigma
        entities.put(0x3C3, "&sigma;");     // greek small letter sigma
        entities.put(0x3C4, "&tau;");       // greek small letter tau
        entities.put(0x3C5, "&upsilon;");   // greek small letter upsilon
        entities.put(0x3C6, "&phi;");       // greek small letter phi
        entities.put(0x3C7, "&chi;");       // greek small letter chi
        entities.put(0x3C8, "&psi;");       // greek small letter psi
        entities.put(0x3C9, "&omega;");     // greek small letter omega
        entities.put(0x3D1, "&thetasym;");  // greek small letter theta symbol
        entities.put(0x3D2, "&upsih;");     // greek upsilon with hook symbol
        entities.put(0x3D6, "&piv;");       // greek pi symbol
        entities.put(0x2002, "&ensp;");     // en space
        entities.put(0x2003, "&emsp;");     // em space
        entities.put(0x2009, "&thinsp;");   // thin space
        entities.put(0x200C, "&zwnj;");     // zero width non-joiner
        entities.put(0x200D, "&zwj;");      // zero width joiner
        entities.put(0x200E, "&lrm;");      // left-to-right mark
        entities.put(0x200F, "&rlm;");      // right-to-left mark
        entities.put(0x2013, "&ndash;");    // en dash
        entities.put(0x2014, "&mdash;");    // em dash
        entities.put(0x2018, "&lsquo;");    // left single quotation mark
        entities.put(0x2019, "&rsquo;");    // right single quotation mark
        entities.put(0x201A, "&sbquo;");    // single low-9 quotation mark
        entities.put(0x201C, "&ldquo;");    // left double quotation mark
        entities.put(0x201D, "&rdquo;");    // right double quotation mark
        entities.put(0x201E, "&bdquo;");    // double low-9 quotation mark
        entities.put(0x2020, "&dagger;");   // dagger
        entities.put(0x2021, "&Dagger;");   // double dagger
        entities.put(0x2022, "&bull;");     // bullet = black small circle
        entities.put(0x2026, "&hellip;");   // horizontal ellipsis
        entities.put(0x2030, "&permil;");   // per mille sign
        entities.put(0x2032, "&prime;");    // prime
        entities.put(0x2033, "&Prime;");    // double prime
        entities.put(0x2039, "&lsaquo;");   // single left-pointing angle quotation mark
        entities.put(0x203A, "&rsaquo;");   // single right-pointing angle quotation mark
        entities.put(0x203E, "&oline;");    // overline
        entities.put(0x2044, "&frasl;");    // fraction slash
        entities.put(0x20AC, "&euro;");     // euro sign
        entities.put(0x2111, "&image;");    // black letter capital I
        entities.put(0x2118, "&weierp;");   // script capital P
        entities.put(0x211C, "&real;");     // black letter capital R
        entities.put(0x2122, "&trade;");    // trademark sign
        entities.put(0x2135, "&alefsym;");  // alef symbol
        entities.put(0x2190, "&larr;");     // leftwards arrow
        entities.put(0x2191, "&uarr;");     // upwards arrow
        entities.put(0x2192, "&rarr;");     // rightwards arrow
        entities.put(0x2193, "&darr;");     // downwards arrow
        entities.put(0x2194, "&harr;");     // left right arrow
        entities.put(0x21B5, "&crarr;");    // downwards arrow with corner leftwards
        entities.put(0x21D0, "&lArr;");     // leftwards double arrow
        entities.put(0x21D1, "&uArr;");     // upwards double arrow
        entities.put(0x21D2, "&rArr;");     // rightwards double arrow
        entities.put(0x21D3, "&dArr;");     // downwards double arrow
        entities.put(0x21D4, "&hArr;");     // left right double arrow
        entities.put(0x2200, "&forall;");   // for all
        entities.put(0x2202, "&part;");     // partial differential
        entities.put(0x2203, "&exist;");    // there exists
        entities.put(0x2205, "&empty;");    // empty set
        entities.put(0x2207, "&nabla;");    // nabla
        entities.put(0x2208, "&isin;");     // element of
        entities.put(0x2209, "&notin;");    // not an element of
        entities.put(0x220B, "&ni;");       // contains as member
        entities.put(0x220F, "&prod;");     // n-ary product
        entities.put(0x2211, "&sum;");      // n-ary summation
        entities.put(0x2212, "&minus;");    // minus sign
        entities.put(0x2217, "&lowast;");   // asterisk operator
        entities.put(0x221A, "&radic;");    // square root
        entities.put(0x221D, "&prop;");     // proportional to
        entities.put(0x221E, "&infin;");    // infinity
        entities.put(0x2220, "&ang;");      // angle
        entities.put(0x2227, "&and;");      // logical and
        entities.put(0x2228, "&or;");       // logical or
        entities.put(0x2229, "&cap;");      // intersection
        entities.put(0x222A, "&cup;");      // union
        entities.put(0x222B, "&int;");      // integral
        entities.put(0x2234, "&there4;");   // therefore
        entities.put(0x223C, "&sim;");      // tilde operator
        entities.put(0x2245, "&cong;");     // approximately equal to
        entities.put(0x2248, "&asymp;");    // almost equal to
        entities.put(0x2260, "&ne;");       // not equal to
        entities.put(0x2261, "&equiv;");    // identical to
        entities.put(0x2264, "&le;");       // less-than or equal to
        entities.put(0x2265, "&ge;");       // greater-than or equal to
        entities.put(0x2282, "&sub;");      // subset of
        entities.put(0x2283, "&sup;");      // superset of
        entities.put(0x2284, "&nsub;");     // not a subset of
        entities.put(0x2286, "&sube;");     // subset of or equal to
        entities.put(0x2287, "&supe;");     // superset of or equal to
        entities.put(0x2295, "&oplus;");    // circled plus
        entities.put(0x2297, "&otimes;");   // circled times
        entities.put(0x22A5, "&perp;");     // up tack
        entities.put(0x22C5, "&sdot;");     // dot operator
        entities.put(0x2308, "&lceil;");    // left ceiling
        entities.put(0x2309, "&rceil;");    // right ceiling
        entities.put(0x230A, "&lfloor;");   // left floor
        entities.put(0x230B, "&rfloor;");   // right floor
        entities.put(0x2329, "&lang;");     // left-pointing angle bracket
        entities.put(0x232A, "&rang;");     // right-pointing angle bracket
        entities.put(0x25CA, "&loz;");      // lozenge
        entities.put(0x2660, "&spades;");   // black spade suit
        entities.put(0x2663, "&clubs;");    // black club suit
        entities.put(0x2665, "&hearts;");   // black heart suit
        entities.put(0x2666, "&diams;");    // black diamond suit
        HTML4_EXTENDED = Collections.unmodifiableMap(entities);
    }

    @NoCoverageGenerated
    private HtmlEntities() {
    }
}
