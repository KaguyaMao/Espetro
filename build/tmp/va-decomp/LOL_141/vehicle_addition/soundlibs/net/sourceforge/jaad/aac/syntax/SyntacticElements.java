/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank.FilterBank;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.CCE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.CPE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ChannelElement;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.DRC;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.DSE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.Element;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.LFE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.PCE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.SCE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyntacticElements {
    static final Logger LOGGER = Logger.getLogger("jaad.SyntacticElements");
    private DecoderConfig config;
    private final FilterBank filterBank;
    private List<CCE> cces = new ArrayList<CCE>();
    private final Map<Element.InstanceTag, Element> elements = new HashMap<Element.InstanceTag, Element>();
    private final List<ChannelElement> audioElements = new ArrayList<ChannelElement>();
    private List<float[]> channels = new ArrayList<float[]>();
    private static final int EXT_FILL = 0;
    private static final int EXT_FILL_DATA = 1;
    private static final int EXT_DATA_ELEMENT = 2;
    private static final int EXT_DYNAMIC_RANGE = 11;
    private static final int EXT_SAC_DATA = 12;
    private static final int EXT_SBR_DATA = 13;
    private static final int EXT_SBR_DATA_CRC = 14;
    private DRC dri;

    private Element newElement(Element.InstanceTag tag) {
        return tag.newElement(this.config);
    }

    private Element getElement(Element.InstanceTag tag) {
        return this.elements.computeIfAbsent(tag, this::newElement);
    }

    public SyntacticElements(DecoderConfig config) {
        this.config = config;
        this.filterBank = new FilterBank(config.isSmallFrameUsed());
        this.startNewFrame();
    }

    public final void startNewFrame() {
        this.audioElements.clear();
        this.cces.clear();
        this.channels.clear();
    }

    public void decode(BitStream in) {
        block21: {
            if (!this.config.getProfile().isErrorResilientProfile()) {
                while (true) {
                    switch (Element.readType(in)) {
                        case SCE: {
                            this.decode(SCE.TAGS, in);
                            break;
                        }
                        case CPE: {
                            this.decode(CPE.TAGS, in);
                            break;
                        }
                        case CCE: {
                            this.decode(CCE.TAGS, in);
                            break;
                        }
                        case LFE: {
                            this.decode(LFE.TAGS, in);
                            break;
                        }
                        case DSE: {
                            this.decode(DSE.TAGS, in);
                            break;
                        }
                        case PCE: {
                            this.decode(PCE.TAGS, in);
                            break;
                        }
                        case FIL: {
                            this.decodeFIL(in);
                            break;
                        }
                        case END: {
                            break block21;
                        }
                    }
                }
            }
            switch (this.config.getChannelConfiguration()) {
                case MONO: {
                    this.decode(SCE.TAGS, in);
                    break;
                }
                case STEREO: {
                    this.decode(CPE.TAGS, in);
                    break;
                }
                case STEREO_PLUS_CENTER: {
                    this.decode(SCE.TAGS, in);
                    this.decode(CPE.TAGS, in);
                    break;
                }
                case STEREO_PLUS_CENTER_PLUS_REAR_MONO: {
                    this.decode(SCE.TAGS, in);
                    this.decode(CPE.TAGS, in);
                    this.decode(LFE.TAGS, in);
                    break;
                }
                case FIVE: {
                    this.decode(SCE.TAGS, in);
                    this.decode(CPE.TAGS, in);
                    this.decode(CPE.TAGS, in);
                    break;
                }
                case FIVE_PLUS_ONE: {
                    this.decode(SCE.TAGS, in);
                    this.decode(CPE.TAGS, in);
                    this.decode(CPE.TAGS, in);
                    this.decode(LFE.TAGS, in);
                    break;
                }
                case SEVEN_PLUS_ONE: {
                    this.decode(SCE.TAGS, in);
                    this.decode(CPE.TAGS, in);
                    this.decode(CPE.TAGS, in);
                    this.decode(CPE.TAGS, in);
                    this.decode(LFE.TAGS, in);
                    break;
                }
                default: {
                    throw new AACException("unsupported channel configuration for error resilience: " + String.valueOf((Object)this.config.getChannelConfiguration()));
                }
            }
        }
        in.byteAlign();
        LOGGER.finest("END");
    }

    private Element decode(List<? extends Element.InstanceTag> tags, BitStream in) {
        int id = in.readBits(4);
        Element.InstanceTag tag = tags.get(id);
        LOGGER.finest(tag.toString());
        Element element = this.getElement(tag);
        element.decode(in);
        if (element instanceof ChannelElement) {
            this.audioElements.add((ChannelElement)element);
        }
        if (element instanceof CCE) {
            this.cces.add((CCE)element);
        }
        if (element instanceof PCE) {
            PCE pce = (PCE)element;
            this.config.setAudioDecoderInfo(pce);
        }
        return element;
    }

    private void decodeFIL(BitStream in) {
        int count = in.readBits(4);
        if (count == 15) {
            count += in.readBits(8) - 1;
        }
        if (count == 0) {
            return;
        }
        in = in.readSubStream(8 * count);
        int type = in.readBits(4);
        switch (type) {
            case 11: {
                this.decodeDynamicRangeInfo(in);
                break;
            }
            case 13: 
            case 14: {
                this.decodeSBR(in, type);
                break;
            }
            case 0: 
            case 1: {
                break;
            }
            case 12: {
                this.decodeSAC(in);
                break;
            }
            case 2: {
                this.decodeExtData(in);
            }
        }
    }

    private ChannelElement getLastAudioElement() {
        int n = this.audioElements.size();
        return n == 0 ? null : this.audioElements.get(n - 1);
    }

    private void decodeSBR(BitStream in, int type) {
        ChannelElement prev = this.getLastAudioElement();
        if (prev != null) {
            prev.decodeSBR(in, type == 14);
        }
    }

    private void decodeDynamicRangeInfo(BitStream in) {
        if (this.dri == null) {
            this.dri = new DRC();
        }
        this.dri.decode(in);
    }

    private void decodeSAC(BitStream in) {
    }

    private void decodeExtData(BitStream in) {
    }

    public List<float[]> process() {
        this.channels.clear();
        for (ChannelElement e : this.audioElements) {
            e.process(this.filterBank, this.cces, this.channels::add);
        }
        if (this.channels.size() == 1 && this.config.getChannelCount() > 1) {
            this.channels.add(this.channels.get(0));
        }
        return this.channels;
    }

    static {
        if (!Utils.isDebug) {
            LOGGER.setLevel(Level.FINE);
        }
    }
}

