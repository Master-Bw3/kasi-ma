package tree.maple.kasima.blocks

import com.mojang.serialization.MapCodec
import net.minecraft.block.*
import net.minecraft.block.Blocks.REDSTONE_BLOCK
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Direction.Axis
import net.minecraft.util.math.Vec3i
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView
import org.joml.Matrix3f
import org.joml.Vector3f
import tree.maple.kasima.blocks.KasimaBlockRegistry.OAK_RUNE_LOG
import tree.maple.kasima.blocks.RuneLog.Companion.RUNE
import tree.maple.kasima.blocks.blockEntities.RuneCoreBlockEntity
import tree.maple.kasima.spellEngine.ASTNode
import tree.maple.kasima.spellEngine.ValidationState
import tree.maple.kasima.spellEngine.compile
import tree.maple.kasima.spellEngine.runes.RuneRegistry


class RuneCore(settings: Settings) : BlockWithEntity(settings.nonOpaque()) {

    init {
        defaultState = defaultState
            .with(RUNE, 0)
            .with(POWERED, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(RUNE)
        builder.add(POWERED)
        builder.add(PillarBlock.AXIS)
        super.appendProperties(builder)
    }

    override fun getCodec(): MapCodec<RuneCore>? {
        return createCodec(::RuneCore)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = RuneCoreBlockEntity(pos, state)

    override fun getRenderType(state: BlockState) = BlockRenderType.MODEL

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        if (!world.isClient) {
            runProgram(world, pos)
        }

        return ActionResult.SUCCESS
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        world: WorldView,
        tickView: ScheduledTickView,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: Random?
    ): BlockState {
        val powered = state.get(POWERED) as Boolean

        if (world.isReceivingRedstonePower(pos) && !powered) {
            if (!world.isClient) runProgram(world as World, pos)

            return state.with(POWERED, true)
        }

        if (!world.isReceivingRedstonePower(pos) && powered) {
            return state.with(POWERED, false)
        }

        return state
    }

    fun runProgram(world: World, corePos: BlockPos) {
        //find where tree starts
        val axis = world.getBlockState(corePos).get(PillarBlock.AXIS)

        val startDirection: Direction? = axis.directions.firstOrNull { direction ->
            val pos = corePos.add(direction.vector)
            world.getBlockState(pos).isOf(OAK_RUNE_LOG)
        }


        if (startDirection != null) {
            val AST = constructASTFromPhysicalTree(
                world,
                visited = mutableSetOf(corePos),
                pos = corePos.add(startDirection.vector),
                prevDirection = startDirection,
            )

            val result = compile(AST.validate()).handle.invoke().toString()

            world.server?.playerManager?.broadcast(Text.literal(result), false)
        }

    }


    fun constructASTFromPhysicalTree(
        world: World,
        visited: MutableSet<BlockPos>,
        pos: BlockPos,
        prevDirection: Direction,
    ): ASTNode<ValidationState.NotValidated> {
        val rune = RuneRegistry.getFromBlockStateID(world.getBlockState(pos).get(RUNE) ?: 0)!!
        val axis = world.getBlockState(pos).get(PillarBlock.AXIS)


        val direction = if (prevDirection.axis == axis) {
            prevDirection
        } else {
            axis.directions.firstOrNull { direction ->
                val offsetPos = pos.add(direction.vector)
                world.getBlockState(offsetPos).isOf(OAK_RUNE_LOG)
            } ?: axis.positiveDirection
        }

        val candidates = direction?.let {
            rune.arguments.indices.map { i ->
                pos.add(direction.vector.multiply(i + 1))
            }
        } ?: listOf()
        if (candidates.any { visited.contains(it) }) throw Exception()
        visited.addAll(candidates)

        return ASTNode(
            rune,
            candidates.map { blockPos ->
                constructASTFromPhysicalTree(
                    world,
                    visited,
                    blockPos,
                    direction,
                )
            })
    }

    companion object {
        val POWERED: BooleanProperty = BooleanProperty.of("powered")
    }
}