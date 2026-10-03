/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.NoteBlockInstrument
 *  net.minecraftforge.eventbus.api.Cancelable
 */
package net.minecraftforge.event.level;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;

public class NoteBlockEvent
extends BlockEvent {
    private int noteId;

    protected NoteBlockEvent(Level world, BlockPos pos, BlockState state, int note) {
        super((LevelAccessor)world, pos, state);
        this.noteId = note;
    }

    public Note getNote() {
        return Note.fromId(this.noteId);
    }

    public Octave getOctave() {
        return Octave.fromId(this.noteId);
    }

    public int getVanillaNoteId() {
        return this.noteId;
    }

    public void setNote(Note note, Octave octave) {
        Preconditions.checkArgument((octave != Octave.HIGH || note == Note.F_SHARP ? 1 : 0) != 0, (Object)"Octave.HIGH is only valid for Note.F_SHARP!");
        this.noteId = note.ordinal() + octave.ordinal() * 12;
    }

    public static enum Note {
        F_SHARP,
        G,
        G_SHARP,
        A,
        A_SHARP,
        B,
        C,
        C_SHARP,
        D,
        D_SHARP,
        E,
        F;

        private static final Note[] values;

        static Note fromId(int id) {
            return values[id % 12];
        }

        static {
            values = Note.values();
        }
    }

    public static enum Octave {
        LOW,
        MID,
        HIGH;


        static Octave fromId(int id) {
            return id < 12 ? LOW : (id == 24 ? HIGH : MID);
        }
    }

    @Cancelable
    public static class Change
    extends NoteBlockEvent {
        private final Note oldNote;
        private final Octave oldOctave;

        public Change(Level world, BlockPos pos, BlockState state, int oldNote, int newNote) {
            super(world, pos, state, newNote);
            this.oldNote = Note.fromId(oldNote);
            this.oldOctave = Octave.fromId(oldNote);
        }

        public Note getOldNote() {
            return this.oldNote;
        }

        public Octave getOldOctave() {
            return this.oldOctave;
        }
    }

    @Cancelable
    public static class Play
    extends NoteBlockEvent {
        private NoteBlockInstrument instrument;

        public Play(Level world, BlockPos pos, BlockState state, int note, NoteBlockInstrument instrument) {
            super(world, pos, state, note);
            this.instrument = instrument;
        }

        public NoteBlockInstrument getInstrument() {
            return this.instrument;
        }

        public void setInstrument(NoteBlockInstrument instrument) {
            this.instrument = instrument;
        }
    }
}

