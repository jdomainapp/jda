package org.jda.example.productsys.services.conveyor.model;

import java.util.Collection;

import org.jda.example.productsys.services.machine.model.Quality;
import org.jda.example.productsys.services.machine.model.io.MachineInput;
import org.jda.example.productsys.services.machine.model.io.MachineOutput;
import org.jda.example.productsys.services.piece.model.Piece;

import java.util.*;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview The conveyor belt.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class Conveyor {

    /*** STATE SPACE **/
    // attributes
    @DAttr(name = "id", type = Type.Integer, id = true, auto = true, optional = false, mutable = false, min = 1)
    private int id;

    @// self.capacity > 0
    DAttr(// self.capacity > 0
    name = "capacity", type = Type.Integer, optional = false, min = 1)
    private Integer capacity;

    @DAttr(name = "nelems", type = Type.Integer, min = 0)
    private Integer nelems;

    @DAttr(name = "machineInput", type = Type.Collection, serialisable = false, filter = @Select(clazz = MachineInput.class))
    @DAssoc(ascName = "Conveyor-assoc-MachineInput", role = "rc", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = MachineInput.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
    private Collection<MachineInput> machineInput;

    @DAttr(name = "machineOutput", type = Type.Collection, serialisable = false, filter = @Select(clazz = MachineOutput.class))
    @DAssoc(ascName = "Conveyor-assoc-MachineOutput", role = "rc", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = MachineOutput.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
    private Collection<MachineOutput> machineOutput;

    @DAttr(name = "piece", type = Type.Collection, serialisable = false, filter = @Select(clazz = Piece.class))
    @DAssoc(ascName = "Conv-assoc-Piece", role = "rc", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @//TODO: self.piece->size() = self.nelems
    Associate(type = Piece.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
    private Collection<Piece> piece;

    @DAttr(name = "quality", type = Type.Collection, serialisable = false, filter = @Select(clazz = Quality.class))
    @DAssoc(ascName = "Conv-assoc-Quality", role = "rc", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = Quality.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
    private Collection<Quality> quality;

    @DAttr(name = "prevConveyor", type = Type.Collection, serialisable = false, filter = @Select(clazz = ConveyorSeq.class))
    @DAssoc(ascName = "ConveyorSeq-assoc-PrevConveyor", role = "rc", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = ConveyorSeq.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
    private Collection<ConveyorSeq> prevConveyor;

    @DAttr(name = "nextConveyor", type = Type.Collection, serialisable = false, filter = @Select(clazz = ConveyorSeq.class))
    @DAssoc(ascName = "ConveyorSeq-assoc-NextConveyor", role = "rc", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = ConveyorSeq.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
    private Collection<ConveyorSeq> nextConveyor;

    /*** BEHAVIOUR SPACE **/
    private static int idCounter;

    @DOpt(type = DOpt.Type.Getter, effects = "result = id")
    @AttrRef(value = "id")
    public int getId() {
        return this.id;
    }

    @DOpt(type = DOpt.Type.AutoAttributeValueGen, effects = "if id = null then idCounter = idCounter + 1 and result = idCounter else if id > idCounter then idCounter = id and result = id else result = id endif endif")
    @AttrRef(value = "id")
    private static int genId(Integer id) {
        Integer val;
        if (id == null) {
            idCounter++;
            val = idCounter;
        } else {
            if (id > idCounter) {
                idCounter = id;
            }
            val = id;
        }
        return val;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = capacity")
    @AttrRef(value = "capacity")
    public Integer getCapacity() {
        return this.capacity;
    }

    @DOpt(type = DOpt.Type.Setter, requires = "capacity >= 1", effects = "self.capacity = capacity")
    @AttrRef(value = "capacity")
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = nelems")
    @AttrRef(value = "nelems")
    public Integer getNelems() {
        return this.nelems;
    }

    @DOpt(type = DOpt.Type.Setter, requires = "nelems >= 0", effects = "self.nelems = nelems")
    @AttrRef(value = "nelems")
    public void setNelems(Integer nelems) {
        this.nelems = nelems;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = machineInput")
    @AttrRef(value = "machineInput")
    public Collection<MachineInput> getMachineInput() {
        return this.machineInput;
    }

    @DOpt(type = DOpt.Type.Setter, effects = "self.machineInput = machineInput")
    @AttrRef(value = "machineInput")
    public void setMachineInput(Collection<MachineInput> machineInput) {
        this.machineInput = machineInput;
    }

    private int machineInputCount;

    @DOpt(type = DOpt.Type.LinkAdder, effects = "machineInput->forAll(o | machineInput@pre->includes(o) or obj = o) and machineInputCount = machineInputCount@pre + (machineInput->size() - machineInput@pre->size())")
    @AttrRef(value = "machineInput")
    public boolean addMachineInput(MachineInput obj) {
        if (!machineInput.contains(obj)) {
            machineInput.add(obj);
            machineInputCount++;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdder, effects = "machineInput->forAll(o | machineInput@pre->includes(o) or obj->includes(o)) and machineInputCount = machineInputCount@pre + (machineInput->size() - machineInput@pre->size())")
    @AttrRef(value = "machineInput")
    public boolean addMachineInput(Collection<MachineInput> obj) {
        for (MachineInput o : obj) {
            if (!machineInput.contains(o)) {
                machineInput.add(o);
                machineInputCount++;
            }
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew, effects = "machineInput->forAll(o | machineInput@pre->includes(o) or obj = o) and machineInputCount = machineInputCount@pre + (machineInput->size() - machineInput@pre->size())")
    @AttrRef(value = "machineInput")
    public boolean addNewMachineInput(MachineInput obj) {
        machineInput.add(obj);
        machineInputCount++;
        return false;
    }

    @DOpt(type = DOpt.Type.LinkUpdater)
    @AttrRef(value = "machineInput")
    public boolean onUpdateMachineInput(MachineInput obj) {
        // TODO: implement this 
        return false;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "if machineInput->includes(obj) then machineInputCount - 1 >= 0 else true endif", effects = "machineInput->forAll(o | machineInput@pre->includes(o) and obj <> o) and machineInputCount = machineInputCount@pre - (machineInput@pre->size() - machineInput->size())")
    @AttrRef(value = "machineInput")
    public boolean onRemoveMachineInput(MachineInput obj) {
        boolean removed = machineInput.remove(obj);
        if (removed)
            machineInputCount--;
        return removed;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "machineInputCount - obj->select(o | machineInput->includes(o))->size() >= 0", effects = "machineInput->forAll(o | machineInput@pre->includes(o) and obj->excludes(o)) and machineInputCount = machineInputCount@pre - (machineInput@pre->size() - machineInput->size())")
    @AttrRef(value = "machineInput")
    public boolean onRemoveMachineInput(Collection<MachineInput> obj) {
        for (MachineInput o : obj) {
            boolean removed = machineInput.remove(o);
            if (removed)
                machineInputCount--;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkCountGetter)
    @AttrRef(value = "machineInputCount")
    public Integer getMachineInputCount() {
        return machineInputCount;
    }

    @DOpt(type = DOpt.Type.LinkCountSetter)
    @AttrRef(value = "machineInputCount")
    public void setMachineInputCount(int machineInputCount) {
        this.machineInputCount = machineInputCount;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = machineOutput")
    @AttrRef(value = "machineOutput")
    public Collection<MachineOutput> getMachineOutput() {
        return this.machineOutput;
    }

    @DOpt(type = DOpt.Type.Setter, effects = "self.machineOutput = machineOutput")
    @AttrRef(value = "machineOutput")
    public void setMachineOutput(Collection<MachineOutput> machineOutput) {
        this.machineOutput = machineOutput;
    }

    private int machineOutputCount;

    @DOpt(type = DOpt.Type.LinkAdder, effects = "machineOutput->forAll(o | machineOutput@pre->includes(o) or obj = o) and machineOutputCount = machineOutputCount@pre + (machineOutput->size() - machineOutput@pre->size())")
    @AttrRef(value = "machineOutput")
    public boolean addMachineOutput(MachineOutput obj) {
        if (!machineOutput.contains(obj)) {
            machineOutput.add(obj);
            machineOutputCount++;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdder, effects = "machineOutput->forAll(o | machineOutput@pre->includes(o) or obj->includes(o)) and machineOutputCount = machineOutputCount@pre + (machineOutput->size() - machineOutput@pre->size())")
    @AttrRef(value = "machineOutput")
    public boolean addMachineOutput(Collection<MachineOutput> obj) {
        for (MachineOutput o : obj) {
            if (!machineOutput.contains(o)) {
                machineOutput.add(o);
                machineOutputCount++;
            }
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew, effects = "machineOutput->forAll(o | machineOutput@pre->includes(o) or obj = o) and machineOutputCount = machineOutputCount@pre + (machineOutput->size() - machineOutput@pre->size())")
    @AttrRef(value = "machineOutput")
    public boolean addNewMachineOutput(MachineOutput obj) {
        machineOutput.add(obj);
        machineOutputCount++;
        return false;
    }

    @DOpt(type = DOpt.Type.LinkUpdater)
    @AttrRef(value = "machineOutput")
    public boolean onUpdateMachineOutput(MachineOutput obj) {
        // TODO: implement this 
        return false;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "if machineOutput->includes(obj) then machineOutputCount - 1 >= 0 else true endif", effects = "machineOutput->forAll(o | machineOutput@pre->includes(o) and obj <> o) and machineOutputCount = machineOutputCount@pre - (machineOutput@pre->size() - machineOutput->size())")
    @AttrRef(value = "machineOutput")
    public boolean onRemoveMachineOutput(MachineOutput obj) {
        boolean removed = machineOutput.remove(obj);
        if (removed)
            machineOutputCount--;
        return removed;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "machineOutputCount - obj->select(o | machineOutput->includes(o))->size() >= 0", effects = "machineOutput->forAll(o | machineOutput@pre->includes(o) and obj->excludes(o)) and machineOutputCount = machineOutputCount@pre - (machineOutput@pre->size() - machineOutput->size())")
    @AttrRef(value = "machineOutput")
    public boolean onRemoveMachineOutput(Collection<MachineOutput> obj) {
        for (MachineOutput o : obj) {
            boolean removed = machineOutput.remove(o);
            if (removed)
                machineOutputCount--;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkCountGetter)
    @AttrRef(value = "machineOutputCount")
    public Integer getMachineOutputCount() {
        return machineOutputCount;
    }

    @DOpt(type = DOpt.Type.LinkCountSetter)
    @AttrRef(value = "machineOutputCount")
    public void setMachineOutputCount(int machineOutputCount) {
        this.machineOutputCount = machineOutputCount;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = piece")
    @AttrRef(value = "piece")
    public Collection<Piece> getPiece() {
        return this.piece;
    }

    @DOpt(type = DOpt.Type.Setter, effects = "self.piece = piece")
    @AttrRef(value = "piece")
    public void setPiece(Collection<Piece> piece) {
        this.piece = piece;
    }

    private int pieceCount;

    @DOpt(type = DOpt.Type.LinkAdder, effects = "piece->forAll(o | piece@pre->includes(o) or obj = o) and pieceCount = pieceCount@pre + (piece->size() - piece@pre->size())")
    @AttrRef(value = "piece")
    public boolean addPiece(Piece obj) {
        if (!piece.contains(obj)) {
            piece.add(obj);
            pieceCount++;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdder, effects = "piece->forAll(o | piece@pre->includes(o) or obj->includes(o)) and pieceCount = pieceCount@pre + (piece->size() - piece@pre->size())")
    @AttrRef(value = "piece")
    public boolean addPiece(Collection<Piece> obj) {
        for (Piece o : obj) {
            if (!piece.contains(o)) {
                piece.add(o);
                pieceCount++;
            }
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew, effects = "piece->forAll(o | piece@pre->includes(o) or obj = o) and pieceCount = pieceCount@pre + (piece->size() - piece@pre->size())")
    @AttrRef(value = "piece")
    public boolean addNewPiece(Piece obj) {
        piece.add(obj);
        pieceCount++;
        return false;
    }

    @DOpt(type = DOpt.Type.LinkUpdater)
    @AttrRef(value = "piece")
    public boolean onUpdatePiece(Piece obj) {
        // TODO: implement this 
        return false;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "if piece->includes(obj) then pieceCount - 1 >= 0 else true endif", effects = "piece->forAll(o | piece@pre->includes(o) and obj <> o) and pieceCount = pieceCount@pre - (piece@pre->size() - piece->size())")
    @AttrRef(value = "piece")
    public boolean onRemovePiece(Piece obj) {
        boolean removed = piece.remove(obj);
        if (removed)
            pieceCount--;
        return removed;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "pieceCount - obj->select(o | piece->includes(o))->size() >= 0", effects = "piece->forAll(o | piece@pre->includes(o) and obj->excludes(o)) and pieceCount = pieceCount@pre - (piece@pre->size() - piece->size())")
    @AttrRef(value = "piece")
    public boolean onRemovePiece(Collection<Piece> obj) {
        for (Piece o : obj) {
            boolean removed = piece.remove(o);
            if (removed)
                pieceCount--;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkCountGetter)
    @AttrRef(value = "pieceCount")
    public Integer getPieceCount() {
        return pieceCount;
    }

    @DOpt(type = DOpt.Type.LinkCountSetter)
    @AttrRef(value = "pieceCount")
    public void setPieceCount(int pieceCount) {
        this.pieceCount = pieceCount;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = quality")
    @AttrRef(value = "quality")
    public Collection<Quality> getQuality() {
        return this.quality;
    }

    @DOpt(type = DOpt.Type.Setter, effects = "self.quality = quality")
    @AttrRef(value = "quality")
    public void setQuality(Collection<Quality> quality) {
        this.quality = quality;
    }

    private int qualityCount;

    @DOpt(type = DOpt.Type.LinkAdder, effects = "quality->forAll(o | quality@pre->includes(o) or obj = o) and qualityCount = qualityCount@pre + (quality->size() - quality@pre->size())")
    @AttrRef(value = "quality")
    public boolean addQuality(Quality obj) {
        if (!quality.contains(obj)) {
            quality.add(obj);
            qualityCount++;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdder, effects = "quality->forAll(o | quality@pre->includes(o) or obj->includes(o)) and qualityCount = qualityCount@pre + (quality->size() - quality@pre->size())")
    @AttrRef(value = "quality")
    public boolean addQuality(Collection<Quality> obj) {
        for (Quality o : obj) {
            if (!quality.contains(o)) {
                quality.add(o);
                qualityCount++;
            }
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew, effects = "quality->forAll(o | quality@pre->includes(o) or obj = o) and qualityCount = qualityCount@pre + (quality->size() - quality@pre->size())")
    @AttrRef(value = "quality")
    public boolean addNewQuality(Quality obj) {
        quality.add(obj);
        qualityCount++;
        return false;
    }

    @DOpt(type = DOpt.Type.LinkUpdater)
    @AttrRef(value = "quality")
    public boolean onUpdateQuality(Quality obj) {
        // TODO: implement this 
        return false;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "if quality->includes(obj) then qualityCount - 1 >= 0 else true endif", effects = "quality->forAll(o | quality@pre->includes(o) and obj <> o) and qualityCount = qualityCount@pre - (quality@pre->size() - quality->size())")
    @AttrRef(value = "quality")
    public boolean onRemoveQuality(Quality obj) {
        boolean removed = quality.remove(obj);
        if (removed)
            qualityCount--;
        return removed;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "qualityCount - obj->select(o | quality->includes(o))->size() >= 0", effects = "quality->forAll(o | quality@pre->includes(o) and obj->excludes(o)) and qualityCount = qualityCount@pre - (quality@pre->size() - quality->size())")
    @AttrRef(value = "quality")
    public boolean onRemoveQuality(Collection<Quality> obj) {
        for (Quality o : obj) {
            boolean removed = quality.remove(o);
            if (removed)
                qualityCount--;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkCountGetter)
    @AttrRef(value = "qualityCount")
    public Integer getQualityCount() {
        return qualityCount;
    }

    @DOpt(type = DOpt.Type.LinkCountSetter)
    @AttrRef(value = "qualityCount")
    public void setQualityCount(int qualityCount) {
        this.qualityCount = qualityCount;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = prevConveyor")
    @AttrRef(value = "prevConveyor")
    public Collection<ConveyorSeq> getPrevConveyor() {
        return this.prevConveyor;
    }

    @DOpt(type = DOpt.Type.Setter, effects = "self.prevConveyor = prevConveyor")
    @AttrRef(value = "prevConveyor")
    public void setPrevConveyor(Collection<ConveyorSeq> prevConveyor) {
        this.prevConveyor = prevConveyor;
    }

    private int prevConveyorCount;

    @DOpt(type = DOpt.Type.LinkAdder, effects = "prevConveyor->forAll(o | prevConveyor@pre->includes(o) or obj = o) and prevConveyorCount = prevConveyorCount@pre + (prevConveyor->size() - prevConveyor@pre->size())")
    @AttrRef(value = "prevConveyor")
    public boolean addPrevConveyor(ConveyorSeq obj) {
        if (!prevConveyor.contains(obj)) {
            prevConveyor.add(obj);
            prevConveyorCount++;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdder, effects = "prevConveyor->forAll(o | prevConveyor@pre->includes(o) or obj->includes(o)) and prevConveyorCount = prevConveyorCount@pre + (prevConveyor->size() - prevConveyor@pre->size())")
    @AttrRef(value = "prevConveyor")
    public boolean addPrevConveyor(Collection<ConveyorSeq> obj) {
        for (ConveyorSeq o : obj) {
            if (!prevConveyor.contains(o)) {
                prevConveyor.add(o);
                prevConveyorCount++;
            }
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew, effects = "prevConveyor->forAll(o | prevConveyor@pre->includes(o) or obj = o) and prevConveyorCount = prevConveyorCount@pre + (prevConveyor->size() - prevConveyor@pre->size())")
    @AttrRef(value = "prevConveyor")
    public boolean addNewPrevConveyor(ConveyorSeq obj) {
        prevConveyor.add(obj);
        prevConveyorCount++;
        return false;
    }

    @DOpt(type = DOpt.Type.LinkUpdater)
    @AttrRef(value = "prevConveyor")
    public boolean onUpdatePrevConveyor(ConveyorSeq obj) {
        // TODO: implement this 
        return false;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "if prevConveyor->includes(obj) then prevConveyorCount - 1 >= 0 else true endif", effects = "prevConveyor->forAll(o | prevConveyor@pre->includes(o) and obj <> o) and prevConveyorCount = prevConveyorCount@pre - (prevConveyor@pre->size() - prevConveyor->size())")
    @AttrRef(value = "prevConveyor")
    public boolean onRemovePrevConveyor(ConveyorSeq obj) {
        boolean removed = prevConveyor.remove(obj);
        if (removed)
            prevConveyorCount--;
        return removed;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "prevConveyorCount - obj->select(o | prevConveyor->includes(o))->size() >= 0", effects = "prevConveyor->forAll(o | prevConveyor@pre->includes(o) and obj->excludes(o)) and prevConveyorCount = prevConveyorCount@pre - (prevConveyor@pre->size() - prevConveyor->size())")
    @AttrRef(value = "prevConveyor")
    public boolean onRemovePrevConveyor(Collection<ConveyorSeq> obj) {
        for (ConveyorSeq o : obj) {
            boolean removed = prevConveyor.remove(o);
            if (removed)
                prevConveyorCount--;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkCountGetter)
    @AttrRef(value = "prevConveyorCount")
    public Integer getPrevConveyorCount() {
        return prevConveyorCount;
    }

    @DOpt(type = DOpt.Type.LinkCountSetter)
    @AttrRef(value = "prevConveyorCount")
    public void setPrevConveyorCount(int prevConveyorCount) {
        this.prevConveyorCount = prevConveyorCount;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = nextConveyor")
    @AttrRef(value = "nextConveyor")
    public Collection<ConveyorSeq> getNextConveyor() {
        return this.nextConveyor;
    }

    @DOpt(type = DOpt.Type.Setter, effects = "self.nextConveyor = nextConveyor")
    @AttrRef(value = "nextConveyor")
    public void setNextConveyor(Collection<ConveyorSeq> nextConveyor) {
        this.nextConveyor = nextConveyor;
    }

    private int nextConveyorCount;

    @DOpt(type = DOpt.Type.LinkAdder, effects = "nextConveyor->forAll(o | nextConveyor@pre->includes(o) or obj = o) and nextConveyorCount = nextConveyorCount@pre + (nextConveyor->size() - nextConveyor@pre->size())")
    @AttrRef(value = "nextConveyor")
    public boolean addNextConveyor(ConveyorSeq obj) {
        if (!nextConveyor.contains(obj)) {
            nextConveyor.add(obj);
            nextConveyorCount++;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdder, effects = "nextConveyor->forAll(o | nextConveyor@pre->includes(o) or obj->includes(o)) and nextConveyorCount = nextConveyorCount@pre + (nextConveyor->size() - nextConveyor@pre->size())")
    @AttrRef(value = "nextConveyor")
    public boolean addNextConveyor(Collection<ConveyorSeq> obj) {
        for (ConveyorSeq o : obj) {
            if (!nextConveyor.contains(o)) {
                nextConveyor.add(o);
                nextConveyorCount++;
            }
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew, effects = "nextConveyor->forAll(o | nextConveyor@pre->includes(o) or obj = o) and nextConveyorCount = nextConveyorCount@pre + (nextConveyor->size() - nextConveyor@pre->size())")
    @AttrRef(value = "nextConveyor")
    public boolean addNewNextConveyor(ConveyorSeq obj) {
        nextConveyor.add(obj);
        nextConveyorCount++;
        return false;
    }

    @DOpt(type = DOpt.Type.LinkUpdater)
    @AttrRef(value = "nextConveyor")
    public boolean onUpdateNextConveyor(ConveyorSeq obj) {
        // TODO: implement this 
        return false;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "if nextConveyor->includes(obj) then nextConveyorCount - 1 >= 0 else true endif", effects = "nextConveyor->forAll(o | nextConveyor@pre->includes(o) and obj <> o) and nextConveyorCount = nextConveyorCount@pre - (nextConveyor@pre->size() - nextConveyor->size())")
    @AttrRef(value = "nextConveyor")
    public boolean onRemoveNextConveyor(ConveyorSeq obj) {
        boolean removed = nextConveyor.remove(obj);
        if (removed)
            nextConveyorCount--;
        return removed;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "nextConveyorCount - obj->select(o | nextConveyor->includes(o))->size() >= 0", effects = "nextConveyor->forAll(o | nextConveyor@pre->includes(o) and obj->excludes(o)) and nextConveyorCount = nextConveyorCount@pre - (nextConveyor@pre->size() - nextConveyor->size())")
    @AttrRef(value = "nextConveyor")
    public boolean onRemoveNextConveyor(Collection<ConveyorSeq> obj) {
        for (ConveyorSeq o : obj) {
            boolean removed = nextConveyor.remove(o);
            if (removed)
                nextConveyorCount--;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkCountGetter)
    @AttrRef(value = "nextConveyorCount")
    public Integer getNextConveyorCount() {
        return nextConveyorCount;
    }

    @DOpt(type = DOpt.Type.LinkCountSetter)
    @AttrRef(value = "nextConveyorCount")
    public void setNextConveyorCount(int nextConveyorCount) {
        this.nextConveyorCount = nextConveyorCount;
    }

    @DOpt(type = DOpt.Type.DataSourceConstructor, requires = "id >= 1 and capacity >= 1 and nelems >= 0", effects = "self.id = genId(id) and self.capacity = capacity and self.nelems = nelems")
    public Conveyor(@AttrRef(value = "id") Integer id, @AttrRef(value = "capacity") Integer capacity, @AttrRef(value = "nelems") Integer nelems) throws ConstraintViolationException {
        this.id = genId(id);
        this.capacity = capacity;
        this.nelems = nelems;
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor, requires = "capacity >= 1 and nelems >= 0", effects = "self.capacity = capacity and self.nelems = nelems and self.id = genId(null)")
    public Conveyor(@AttrRef(value = "capacity") Integer capacity, @AttrRef(value = "nelems") Integer nelems) throws ConstraintViolationException {
        this.id = genId(null);
        this.capacity = capacity;
        this.nelems = nelems;
    }

    @DOpt(type = DOpt.Type.RequiredConstructor, requires = "capacity >= 1", effects = "self.capacity = capacity and self.id = genId(null)")
    public Conveyor(@AttrRef(value = "capacity") Integer capacity) throws ConstraintViolationException {
        this.id = genId(null);
        this.capacity = capacity;
        this.nelems = null;
    }

    @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
    public static void synchWithSource(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal) throws ConstraintViolationException {
        String attribName = attrib.name();
        if (attribName.equals("id")) {
            int maxIdVal = (Integer) maxVal;
            if (maxIdVal > idCounter)
                idCounter = maxIdVal;
        }
    }
}
